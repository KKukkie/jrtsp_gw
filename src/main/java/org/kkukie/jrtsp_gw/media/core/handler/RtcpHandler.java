package org.kkukie.jrtsp_gw.media.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.kkukie.jrtsp_gw.media.core.scheduler.ServiceScheduler;
import org.kkukie.jrtsp_gw.media.core.scheduler.base.Scheduler;
import org.kkukie.jrtsp_gw.media.core.stream.dtls.DtlsHandler;
import org.kkukie.jrtsp_gw.media.core.stream.rtcp.*;
import org.kkukie.jrtsp_gw.media.core.stream.rtp.RtpPacket;
import org.kkukie.jrtsp_gw.media.core.stream.rtp.channels.PacketHandler;
import org.kkukie.jrtsp_gw.media.core.stream.rtp.channels.PacketHandlerException;
import org.kkukie.jrtsp_gw.media.core.stream.rtp.statistics.RtpStatistics;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class RtcpHandler implements PacketHandler {

    private final String conferenceId;

    /** Time (in ms) between SSRC Task executions */
    private static final long SSRC_TASK_DELAY = 7000;

    /* Core elements */
    private final DatagramSocket datagramSocket;
    private final ByteBuffer byteBuffer;
    private int pipelinePriority;

    /* Scheduler */
    private final Scheduler serviceScheduler;
    private TxTask scheduledTask;
    private Future<?> reportTaskFuture;
    private final SsrcTask ssrcTask;
    private Future<?> ssrcTaskFuture;

    /* RTCP elements */
    private final RtpStatistics statistics;

    private Consumer<RtcpInfo> rtcpRecvCallback = whatever -> {};

    /** The elapsed time (milliseconds) since an RTCP packet was transmitted */
    private long tp;
    /** The time interval (milliseconds) until next scheduled transmission time of an RTCP packet */
    private long tn;

    /** Flag that is true if the application has not yet sent an RTCP packet */
    private final AtomicBoolean initial;

    /** Flag that is true once the handler joined an RTP session */
    private final AtomicBoolean joined;

    /* WebRTC */
    /** Checks whether communication of this channel is secure. WebRTC calls only. */
    private boolean secure;

    /** Handles the DTLS handshake and encodes/decodes secured packets. For WebRTC calls only. */
    private DtlsHandler dtlsHandler;

    // Media Type (true:audio, false:video)
    private final boolean mediaType;
    public static final String AUDIO_TYPE = "audio";

    private final SocketAddress remoteAddress;

    public RtcpHandler(String conferenceId, DatagramSocket datagramSocket, final RtpStatistics statistics, String mediaType, SocketAddress remoteAddress) {
        this.conferenceId = conferenceId;
        this.datagramSocket = datagramSocket;

        this.mediaType = mediaType.equals(AUDIO_TYPE);
        this.remoteAddress = remoteAddress;

        // Scheduler
        this.serviceScheduler = new ServiceScheduler();
        this.ssrcTask = new SsrcTask();

        // core stuff
        this.pipelinePriority = 0;
        this.byteBuffer = ByteBuffer.allocateDirect(RtpPacket.RTP_PACKET_MAX_SIZE);

        // rtcp stuff
        this.statistics = statistics;
        this.scheduledTask = null;
        this.tp = 0;
        this.tn = -1;
        this.initial = new AtomicBoolean(true);
        this.joined = new AtomicBoolean(false);

        // webrtc
        this.secure = false;
        this.dtlsHandler = null;
    }

    @Override
    public void destroy() throws Exception {
        leaveRtpSession();
        stop();
    }

    public void start() {
        serviceScheduler.start();
    }

    public void stop() {
        serviceScheduler.stop();
    }

    @Override
    public int getPipelinePriority() {
        return pipelinePriority;
    }

    public void setPipelinePriority(int pipelinePriority) {
        this.pipelinePriority = pipelinePriority;
    }

    /**
     * Gets the time interval between the current time and another time stamp.
     *
     * @param timestamp The time stamp, in milliseconds, to compare to the current time
     * @return The interval of time between both time stamps, in milliseconds.
     */
    private long resolveInterval(long timestamp) {
        long interval = timestamp - this.statistics.getCurrentTime();
        return (interval < 0) ? 0 : interval;
    }

    /**
     * Gets whether the handler is in initial stage.<br>
     * The handler is in initial stage until it has sent at least one RTCP packet during the current RTP session.
     *
     * @return true if not rtcp packet has been sent, false otherwise.
     */
    public boolean isInitial() {
        return initial.get();
    }

    /**
     * Gets whether the handler is currently joined to an RTP Session.
     *
     * @return Return true if joined. Otherwise, returns false.
     */
    public boolean isJoined() {
        return joined.get();
    }

    /**
     * Upon joining the session, the participant initializes tp to 0, tc to 0, senders to 0, pmembers to 1, members to 1,
     * we_sent to false, rtcp_bw to the specified fraction of the session bandwidth, initial to true, and avg_rtcp_size to the
     * probable size of the first RTCP packet that the application will later construct.
     *
     * The calculated interval T is then computed, and the first packet is scheduled for time tn = T. This means that a
     * transmission timer is set which expires at time T. Note that an application MAY use any desired approach for implementing
     * this timer.
     *
     * The participant adds its own SSRC to the member table.
     */
    public void joinRtpSession() {
        if (!this.joined.get()) {
            // Schedule first RTCP packet
            long t = this.statistics.rtcpInterval(this.initial.get());
            this.tn = this.statistics.getCurrentTime() + t;
            scheduleRtcp(this.tn, RtcpPacketType.RTCP_REPORT);

            // Start SSRC timeout timer
            this.ssrcTaskFuture = this.serviceScheduler.scheduleWithFixedDelay(ssrcTask, SSRC_TASK_DELAY, SSRC_TASK_DELAY, TimeUnit.MILLISECONDS);
            this.joined.set(true);

            log.debug("|RtcpHandler({})| Joined the rtp session.", conferenceId);
        }
    }

    public void leaveRtpSession() {
        if (this.joined.get()) {
            this.joined.set(false);

            /*
             * When the participant decides to leave the system, tp is reset to tc, the current time, members and pmembers are
             * initialized to 1, initial is set to 1, we_sent is set to false, senders is set to 0, and avg_rtcp_size is set to
             * the size of the compound BYE packet.
             *
             * The calculated interval T is computed. The BYE packet is then scheduled for time tn = tc + T.
             */
            this.tp = this.statistics.getCurrentTime();
            this.statistics.resetMembers();
            this.initial.set(true);
            this.statistics.clearSenders();

            // XXX Sending the BYE packet NOW, since channel will be closed - hrosa
            // long t = this.statistics.rtcpInterval(initial);
            // this.tn = resolveDelay(t);
            // this.scheduleRtcp(this.tn, RtcpPacketType.RTCP_BYE);

            // cancel scheduled task and schedule BYE now
            if(this.reportTaskFuture != null) {
                this.reportTaskFuture.cancel(true);
            }

            // Send BYE
            // Do not run in separate thread so channel can be properly closed by the owner of this handler
            this.statistics.setRtcpPacketType(RtcpPacketType.RTCP_BYE);
            this.scheduledTask = new TxTask(RtcpPacketType.RTCP_BYE);
            this.scheduledTask.run();

            log.debug("|RtcpHandler({})| Leaved the rtp session.", conferenceId);
        }
    }

    /**
     * Gets the time interval until the next report is sent.
     *
     * @return Returns the time interval in milliseconds until the report is sent. Returns -1 if no report is currently
     *         scheduled.
     */
    public long getNextScheduledReport() {
        long delay = this.tn - statistics.getCurrentTime();
        return delay < 0 ? -1 : delay;
    }

    /**
     * Schedules an event to occur at a certain time.
     *
     * @param timestamp The time (in milliseconds) when the event should be fired
     * @param packetType RtcpPacketType
     */
    private void scheduleRtcp(long timestamp, RtcpPacketType packetType) {
        // Create the task and schedule it
        long interval = resolveInterval(timestamp);
        this.scheduledTask = new TxTask(packetType);

        try {
            this.reportTaskFuture = this.serviceScheduler.schedule(this.scheduledTask, interval, TimeUnit.MILLISECONDS);
            // Let the RTP handler know what is the type of scheduled packet
            this.statistics.setRtcpPacketType(packetType);
        } catch (IllegalStateException e) {
            log.warn("|RtcpHandler({})| RTCP timer already canceled. No more reports will be scheduled.", conferenceId);
        }
    }

    private void scheduleNow(RtcpPacketType packetType) {
        this.scheduledTask = new TxTask(packetType);
        try {
            this.reportTaskFuture = this.serviceScheduler.submit(this.scheduledTask);
            // Let the RTP handler know what is the type of scheduled packet
            this.statistics.setRtcpPacketType(packetType);
        } catch (IllegalStateException e) {
            log.warn("|RtcpHandler({})| RTCP timer already canceled. No more reports will be scheduled.", conferenceId);
        }
    }

    /**
     * Re-schedules a previously scheduled event.
     *
     * @param timestamp The time stamp (in milliseconds) of the rescheduled event
     */
    private void rescheduleRtcp(TxTask task, long timestamp) {
        // Cancel current execution of the task
        this.reportTaskFuture.cancel(true);

        // Re-schedule task execution
        long interval = resolveInterval(timestamp);
        try {
            this.reportTaskFuture = this.serviceScheduler.schedule(task, interval, TimeUnit.MILLISECONDS);
        } catch (IllegalStateException e) {
            log.warn("|RtcpHandler({})| RTCP timer already canceled. Scheduled report was canceled and cannot be re-scheduled.", conferenceId);
        }
    }

    /**
     * Secures the channel, meaning all traffic is SRTCP.
     *
     * SRTCP handlers will only be available to process traffic after a DTLS handshake is completed.
     *
     */
    public void enableSRTCP(DtlsHandler dtlsHandler) {
        this.dtlsHandler = dtlsHandler;
        this.secure = true;
    }

    /**
     * Disables secure layer on the channel, meaning all traffic is treated as plain RTCP.
     */
    public void disableSRTCP() {
        this.dtlsHandler = null;
        this.secure = false;
    }

    @Override
    public boolean canHandle(byte[] packet) {
        return canHandle(packet, packet.length, 0);
    }

    @Override
    public boolean canHandle(byte[] packet, int dataLength, int offset) {
        byte b0 = packet[offset];
        int b0Int = b0 & 0xff;

        // Differentiate between RTP, STUN and DTLS packets in the pipeline
        // https://tools.ietf.org/html/rfc5764#section-5.1.2
        if (b0Int > 127 && b0Int < 192) {
            // RTP version field must equal 2
            int version = (b0 & 0xC0) >> 6;
            if (version == RtpPacket.VERSION) {
                // The payload type field of the first RTCP packet in a compound
                // packet must be equal to SR or RR.
                int type = packet[offset + 1] & 0x000000FF;
                if (type == RtcpHeader.RTCP_SR || type == RtcpHeader.RTCP_RR) {
                    /*
                     * The padding bit (P) should be zero for the first packet of a compound RTCP packet because padding should
                     * only be applied, if it is needed, to the last packet.
                     */
                    int padding = (packet[offset] & 0x20) >> 5;
                    /*
                     * The length fields of the individual RTCP packets must add up to the overall length of the
                     * compound RTCP packet as received. This is a fairly strong check.
                     */
                    return padding == 0;
                }
            }
        }
        return false;
    }

    @Override
    public byte[] handle(byte[] packet, InetSocketAddress localPeer, InetSocketAddress remotePeer)
            throws PacketHandlerException {
        return handle(packet, packet.length, 0, localPeer, remotePeer);
    }

    @Override
    public byte[] handle(byte[] packet, int dataLength, int offset, InetSocketAddress localPeer, InetSocketAddress remotePeer)
            throws PacketHandlerException {
        // Do NOT handle data if have not joined RTP session
        if(!this.joined.get()) {
            return null;
        }

        // Do NOT handle data while DTLS handshake is ongoing. WebRTC calls only.
        if (this.secure && !this.dtlsHandler.isHandshakeComplete()) {
            return null;
        }

        // Check if incoming packet is supported by the handler
        if (!canHandle(packet, dataLength, offset)) {
            log.warn("|RtcpHandler({})| Cannot handle incoming packet!", conferenceId);
            throw new PacketHandlerException("Cannot handle incoming packet");
        }

        // Decode the RTCP compound packet
        RtcpPacket rtcpPacket = new RtcpPacket();
        byte[] decoded = null;
        if (this.secure) {
            decoded = this.dtlsHandler.decodeRTCP(packet, offset, dataLength);
            if (decoded == null || decoded.length == 0) {
                log.warn("|RtcpHandler({})| Could not decode incoming SRTCP packet. Packet will be dropped.", conferenceId);
                return null;
            }
            rtcpPacket.decode(decoded, 0);
        } else {
            rtcpPacket.decode(packet, offset);
        }

        // Trace incoming RTCP report
        if (log.isTraceEnabled()) {
            log.trace("|RtcpHandler({})| RECEIVED RTCP\n[{}]", conferenceId, rtcpPacket);
        }

        if (this.secure && decoded != null) {
            this.onRtcpReceive(new RtcpInfo(rtcpPacket, decoded, remotePeer, localPeer, this.mediaType));
        } else {
            this.onRtcpReceive(new RtcpInfo(rtcpPacket, packet, remotePeer, localPeer, this.mediaType));
        }


        // Upgrade RTCP statistics
        this.statistics.onRtcpReceive(rtcpPacket);

        if (RtcpPacketType.RTCP_BYE.equals(rtcpPacket.getPacketType())) {
            if (RtcpPacketType.RTCP_REPORT.equals(this.scheduledTask.getPacketType())) {
                /*
                 * To make the transmission rate of RTCP packets more adaptive to changes in group membership, the following
                 * "reverse reconsideration" algorithm SHOULD be executed when a BYE packet is received that reduces members to
                 * a value less than members
                 */
                if (this.statistics.getMembers() < this.statistics.getPmembers()) {
                    long tc = this.statistics.getCurrentTime();
                    this.tn = tc + (this.statistics.getMembers() / this.statistics.getPmembers()) * (this.tn - tc);
                    this.tp = tc - (this.statistics.getMembers() / this.statistics.getPmembers()) * (tc - this.tp);

                    // Reschedule the next report for time tn
                    rescheduleRtcp(this.scheduledTask, this.tn);
                    this.statistics.confirmMembers();
                }
            }
        }

        // RTCP handler does not send replies
        return null;
    }

    public void sendRtcpRawPacket (byte[] data) throws IOException {
        // Do NOT attempt to send packet if have not joined RTP session
        if (!this.joined.get()) {
            return;
        }

        // DO NOT attempt to send packet while DTLS handshake is ongoing
        if (!this.dtlsHandler.isHandshakeComplete()) {
            return;
        }

        if (this.datagramSocket != null) {
            if (this.secure) {
                data = this.dtlsHandler.encodeRTCP(data, 0, data.length);
            }

            // prepare buffer
            byteBuffer.clear();
            byteBuffer.rewind();
            byteBuffer.put(data, 0, data.length);
            byteBuffer.flip();
            byteBuffer.rewind();

            byte[] sendData = byteBuffer.array();
            this.datagramSocket.send(
                    new DatagramPacket(sendData, sendData.length, remoteAddress)
            );

            // trace outgoing RTCP report
            if (log.isTraceEnabled()) {
                log.trace("|RtcpHandler({})| SENDING RTCP [dataLen={}]", conferenceId, data.length);
            }

        } else {
            if (log.isDebugEnabled()) {
                log.debug("|RtcpHandler({})| Could not send raw rtcp packet.", conferenceId);
            }
        }
    }

    public void sendRtcpPacket(RtcpPacket packet) throws IOException {
        // Do NOT attempt to send packet if have not joined RTP session
        if(!this.joined.get()) {
            return;
        }

        // DO NOT attempt to send packet while DTLS handshake is ongoing
        if (!this.dtlsHandler.isHandshakeComplete()) {
            return;
        }

        RtcpPacketType type = packet.hasBye() ? RtcpPacketType.RTCP_BYE : RtcpPacketType.RTCP_REPORT;

        if (this.datagramSocket != null) {
            // decode packet
            byte[] data = new byte[RtpPacket.RTP_PACKET_MAX_SIZE];
            packet.encode(data, 0);
            int dataLength = packet.getSize();

            // If channel is secure, convert RTCP packet to SRTCP. WebRTC calls only.
            if (this.secure) {
                data = this.dtlsHandler.encodeRTCP(data, 0, dataLength);
                dataLength = data.length;
            }

            // prepare buffer
            byteBuffer.clear();
            byteBuffer.rewind();
            byteBuffer.put(data, 0, dataLength);
            byteBuffer.flip();
            byteBuffer.rewind();

            // trace outgoing RTCP report
            if (log.isTraceEnabled()) {
                log.trace("|RtcpHandler({})| SENDING RTCP\n[{}]", conferenceId, packet);
            }

            // Make double sure channel is still open and connected before sending
            if (datagramSocket != null) {
                // send packet
                // XXX Should register on RTP statistics IF sending fails!
                byte[] sendData = byteBuffer.array();
                this.datagramSocket.send(
                        new DatagramPacket(sendData, sendData.length, remoteAddress)
                );
            } else {
                // cancel packet transmission
                if (log.isDebugEnabled()) {
                    if (this.datagramSocket.isClosed()) {
                        log.debug("|RtcpHandler({})| Channel is closed.", conferenceId);
                    } else if (!this.datagramSocket.isConnected()) {
                        log.debug("|RtcpHandler({})| RtChannel is disconnected.", conferenceId);
                    }
                    log.debug("|RtcpHandler({})| Could not send {} packet.", conferenceId, type);
                }
                return;
            }
            // If we send at least one RTCP packet then initial = false
            this.initial.set(false);

            // update RTCP statistics
            this.statistics.onRtcpSent(packet);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("|RtcpHandler({})| Could not send {} packet.", conferenceId, type);
            }
        }
    }

    public synchronized void reset() {
        if (joined.get()) {
            throw new IllegalStateException("|RtcpHandler(" + conferenceId + ")| Cannot reset handler while is part of active RTP session.");
        }

        if (this.reportTaskFuture != null) {
            this.reportTaskFuture.cancel(false);
            this.reportTaskFuture = null;
            this.scheduledTask = null;
        }

        if (this.ssrcTaskFuture != null) {
            this.ssrcTaskFuture.cancel(false);
            this.ssrcTaskFuture = null;
        }

        this.tp = 0;
        this.tn = -1;
        this.initial.set(true);
        this.joined.set(false);

        if (this.secure) {
            disableSRTCP();
        }
    }

    /**
     * Disconnects and closes the datagram channel used to send and receive RTCP traffic.
     */
    private void closeChannel() {
        if (this.datagramSocket != null) {
            if (!this.datagramSocket.isClosed()) {
                this.datagramSocket.disconnect();
            }

            this.datagramSocket.close();
        }
    }

    public int compareTo(PacketHandler o) {
        if (o == null) {
            return 1;
        }
        return this.getPipelinePriority() - o.getPipelinePriority();
    }

    // TODO
    public void sendRtcpPacket (RtcpPacketType rtcpPacketType) {
        TxTask txTask = new TxTask(rtcpPacketType);
        txTask.run();
    }

    /**
     * Runnable task responsible for sending RTCP packets.
     */
    private class TxTask implements Runnable {

        private final RtcpPacketType packetType;

        public TxTask(RtcpPacketType packetType) {
            this.packetType = packetType;
        }

        public RtcpPacketType getPacketType() {
            return this.packetType;
        }

        @Override
        public void run() {
            try {
                onExpire();
            } catch (IOException e) {
                log.error("|RtcpHandler({})| Cannot send scheduled RTCP report. Stopping handler.", conferenceId);
                reset();
            }
        }

        /**
         * This function is responsible for deciding whether to send an RTCP report or BYE packet now, or to reschedule
         * transmission.
         *
         * It is also responsible for updating the pmembers, initial, tp, and avg_rtcp_size state variables. This function
         * should be called upon expiration of the event timer used by Schedule().
         *
         * @throws IOException When a packet cannot be sent over the datagram channel
         */
        private void onExpire() throws IOException {
            long t;
            long tc = statistics.getCurrentTime();
            switch (this.packetType) {
                case RTCP_REPORT:
                    if (joined.get()) {
                        t = statistics.rtcpInterval(RtcpHandler.this.initial.get());
                        RtcpHandler.this.tn = RtcpHandler.this.tp + t;

                        if (tn <= tc) {
                            // Send currently scheduled packet and update statistics
                            RtcpPacket report = RtcpPacketFactory.buildReport(statistics);
                            sendRtcpPacket(report);

                            tp = tc;

                            /*
                             * We must redraw the interval. Don't reuse the one computed above, since its not actually
                             * distributed the same, as we are conditioned on it being small enough to cause a packet to be
                             * sent.
                             */
                            t = statistics.rtcpInterval(initial.get());
                            tn = tc + t;
                        }

                        // schedule next packet (only if still in RTP session)
                        scheduleRtcp(tn, RtcpPacketType.RTCP_REPORT);
                        statistics.confirmMembers();
                    }
                    break;

                case RTCP_BYE:
                    /*
                     * In the case of a BYE, we use "timer reconsideration" to reschedule the transmission of the BYE if
                     * necessary
                     */
                    // XXX decided to send RTCP BYE right away - hrosa
                    // t = statistics.rtcpInterval(initial);
                    t = 0;
                    tn = tp + t;

                    // Send BYE and stop scheduling further packets
                    RtcpPacket bye = RtcpPacketFactory.buildBye(statistics);

                    // Set the avg_packet_size to the size of the compound BYE packet
                    statistics.setRtcpAvgSize(bye.getSize());

                    // Send the BYE and close channel
                    sendRtcpPacket(bye);
                    break;

                default:
                    log.warn("|RtcpHandler({})| Unknown scheduled event type!", conferenceId);
                    break;
            }
        }

    }


    public void onRtcpReceive (RtcpInfo rtcpInfo){
        rtcpRecvCallback.accept(rtcpInfo);
    }

    public void setRtcpRecvCallback (Consumer<RtcpInfo> rtcpRecvCallback) {
        this.rtcpRecvCallback = rtcpRecvCallback;
    }

    /**
     * Runnable task responsible for checking timeouts of registered SSRC.
     */
    private class SsrcTask implements Runnable {

        @Override
        public void run() {
            statistics.isSenderTimeout();
        }

    }

    public RtpStatistics getStatistics() {
        return statistics;
    }

    public DtlsHandler getDtlsHandler() {
        return dtlsHandler;
    }
}

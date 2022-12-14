package org.kkukie.jrtsp_gw.media.core.stream.rtsp.rtcp.type.extended.feedback.payloadspecific;

import org.kkukie.jrtsp_gw.media.core.stream.rtsp.rtcp.type.extended.feedback.RtcpFeedback;
import org.kkukie.jrtsp_gw.media.core.stream.rtsp.rtcp.type.extended.feedback.base.RtcpFeedbackMessageHeader;

public class RtcpFullIntraRequest extends RtcpFeedback { // Full INTRA-frame Request

    /**
     * FIR is also known as an "instantaneous decoder refresh request",
     * "fast video update request" or "video fast update request".
     * <p>
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |V=2|P|   MBZ   |  PT=RTCP_FIR  |           length              |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                              SSRC                             |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * <p>
     * <p>
     * Long-standing experience of the conversational video
     * conferencing industry suggests that there is a need for a few
     * additional feedback messages, to support centralized multipoint
     * conferencing efficiently.
     * <p>
     * Some of the messages have applications
     * beyond centralized multipoint, and this is indicated in the
     * description of the message.
     * <p>
     * A Full Intra Request (FIR) Command, when received by the designated
     * media sender, requires that the media sender sends a Decoder Refresh
     * Point (see section 2.2) at the earliest opportunity.
     * The evaluation of such an opportunity includes the current encoder coding strategy
     * and the current available network resources.
     * <p>
     * The purpose is to speed up refreshment of the
     * video in those situations where their use is feasible.
     * <p>
     * If using immediate feedback mode,
     * the repetition SHOULD wait at least one RTT before being sent.
     * In early or regular RTCP mode, the repetition is sent in the next regular RTCP packet.
     * > ??????????????? FIR ????????? ?????? RTCP ?????? ?????? ????????? ?????? ???????????????.
     * <p>
     * In conjunction with video codecs, FIR messages typically trigger the
     * sending of full intra or IDR pictures.  Both are several times larger
     * than predicted (inter) pictures.  Their size is independent of the
     * time they are generated.
     * > FIR ???????????? ?????? ????????? ??????????????? ????????? i-frame ??? idr ???????????? ???????????? ???????????????.
     * ?????? ????????? ?????? ???????????? ????????? ?????? ??????????????? ??? ?????????, ????????? ????????? ???????????? ?????????.
     * <p>
     * if the sending frame rate is 10 fps,
     * and an intra picture is assumed to be 10 times as big as an inter picture,
     * then a full second of latency has to be accepted.
     * >> Intra-frame means that all the compression is done within that
     * single frame and generates what is sometimes referred to as an i-frame.
     * >> Inter-frame refers to compression that
     * takes place across two or more frames,
     * where the encoding scheme only keeps the information that changes between frames.
     * > ????????? ?????? ????????? 10 fps ??????, intra-frame ??? inter-frame ?????? 10 ??? ??? ????????? ????????? ??????,
     * full second of latency(?) ??? ????????????.
     * <p>
     * Mandating a maximum delay for completing the sending of a decoder
     * refresh point would be desirable from an application viewpoint, but
     * is problematic from a congestion control point of view.  "As soon as
     * possible" as mentioned above appears to be a reasonable compromise.
     * > ?????????????????? ??????????????? ????????? ??? decoder refresh point ??? ??????????????? ??????.
     * ????????? ???????????? ???????????? ????????? ?????????.
     * <p>
     * In environments where the sender has no control over the codec (e.g.,
     * when streaming pre-recorded and pre-coded content), the reaction to
     * this command cannot be specified.  One suitable reaction of a sender
     * would be to skip forward in the video bit stream to the next decoder
     * refresh point.
     * > ????????? ??????????????? ?????? ??????(?????????)??? ?????? ?????? ????????? ?????? ?????????, FIR ??? ?????? ???????????? ?????????.
     * ????????? ?????? ?????? ??????????????? ?????? decoder refresh point ?????? ????????? ?????? ???????????? ??????????????????.
     * <p>
     * However, a session that predominantly handles pre-coded
     * content is not expected to use FIR at all.
     * > ????????? ?????? ????????? ????????? ???????????? ????????? ???????????? FIR ????????? ????????? ?????????.
     * <p>
     * <p>
     * Picture Loss Indication informs the decoder about the loss of a picture and
     * hence the likelihood of misalignment of the reference pictures
     * between the encoder and decoder.
     * > PLI ????????? ???????????? ?????????
     * ????????? ????????? ?????? ?????? ???????????? ????????? ????????? ????????? ???????????? ???????????? ???????????? ?????? ????????????.
     */

    ////////////////////////////////////////////////////////////
    // VARIABLES
    public static final int MIN_LENGTH = RtcpFeedbackMessageHeader.LENGTH; // bytes


    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    public RtcpFullIntraRequest(RtcpFeedbackMessageHeader rtcpFeedbackMessageHeader) {
        super(rtcpFeedbackMessageHeader);
    }

    public RtcpFullIntraRequest() {
    }

    public RtcpFullIntraRequest(byte[] data) {
        super(data);
    }
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // FUNCTIONS


    ////////////////////////////////////////////////////////////

}

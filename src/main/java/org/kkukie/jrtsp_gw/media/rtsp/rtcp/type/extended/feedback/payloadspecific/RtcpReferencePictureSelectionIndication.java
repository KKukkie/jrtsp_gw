package org.kkukie.jrtsp_gw.media.rtsp.rtcp.type.extended.feedback.payloadspecific;


import org.kkukie.jrtsp_gw.media.rtsp.rtcp.type.extended.feedback.RtcpFeedback;
import org.kkukie.jrtsp_gw.media.rtsp.rtcp.type.extended.feedback.base.RtcpFeedbackMessageHeader;

public class RtcpReferencePictureSelectionIndication extends RtcpFeedback {

    /**
     * Reference Picture Selection Indication
     * <p>
     * The RPSI FB message is identified by PT=PSFB and FMT=3.
     * <p>
     * There MUST be exactly one RPSI contained in the FCI field.
     * <p>
     * (Syntax of the Reference Picture Selection Indication (RPSI))
     * <p>
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |      PB       |0| Payload Type|    Native RPSI bit string     |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |   defined per codec          ...                | Padding (0) |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * <p>
     * PB: 8 bits
     * The number of unused bits required to pad the length of the RPSI
     * message to a multiple of 32 bits.
     * <p>
     * 0:  1 bit
     * MUST be set to zero upon transmission and ignored upon reception.
     * <p>
     * Payload Type: 7 bits
     * Indicates the RTP payload type in the context of which the native
     * RPSI bit string MUST be interpreted.
     * <p>
     * Native RPSI bit string: variable length
     * The RPSI information as natively defined by the video codec.
     * <p>
     * Padding: #PB bits
     * A number of bits set to zero to fill up the contents of the RPSI
     * message to the next 32-bit boundary.  The number of padding bits
     * MUST be indicated by the PB field.
     */

    ////////////////////////////////////////////////////////////
    // VARIABLES
    public static final int MIN_LENGTH = RtcpFeedbackMessageHeader.LENGTH; // bytes


    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    public RtcpReferencePictureSelectionIndication(RtcpFeedbackMessageHeader rtcpFeedbackMessageHeader) {
        super(rtcpFeedbackMessageHeader);
    }

    public RtcpReferencePictureSelectionIndication() {
    }

    public RtcpReferencePictureSelectionIndication(byte[] data) {
        super(data);
    }
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // FUNCTIONS


    ////////////////////////////////////////////////////////////

}

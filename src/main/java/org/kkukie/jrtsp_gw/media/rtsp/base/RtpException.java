package org.kkukie.jrtsp_gw.media.rtsp.base;

public class RtpException extends Exception {

    public static final String OUT_OF_RANGE = "Method parameter value is out of range.";

    public RtpException(String exceptionType) {

        super(exceptionType);

    }

    public RtpException(String error, Exception wrappedException) {
        super(error, wrappedException);
    }

}

package org.mipams.jpegsnack.util;

import org.mipams.jumbf.util.MipamsException;

public class JpegSnackException extends MipamsException {

    public JpegSnackException() {
        super();
    }

    public JpegSnackException(String message) {
        super(message);
    }

    public JpegSnackException(String message, Throwable cause) {
        super(message, cause);
    }

    public JpegSnackException(Throwable cause) {
        super(cause);
    }
}

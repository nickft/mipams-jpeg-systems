package org.mipams.jumbf.util;

public class UnsupportedContentTypeException extends MipamsException {

    public UnsupportedContentTypeException() {
        super();
    }

    public UnsupportedContentTypeException(String message) {
        super(message);
    }

    public UnsupportedContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedContentTypeException(Throwable cause) {
        super(cause);
    }
}

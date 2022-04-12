package org.mipams.jumbf.core.util;

public class CorruptedJumbfFileException extends MipamsException {

    public CorruptedJumbfFileException() {
        super();
    }

    public CorruptedJumbfFileException(String message) {
        super(message);
    }

    public CorruptedJumbfFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptedJumbfFileException(Throwable cause) {
        super(cause);
    }
}
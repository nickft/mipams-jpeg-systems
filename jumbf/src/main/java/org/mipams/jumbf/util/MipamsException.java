package org.mipams.jumbf.util;

import java.util.logging.Logger;
import java.util.logging.Level;

public class MipamsException extends Exception {

    private static final Logger logger = Logger.getLogger(MipamsException.class.getName());

    public MipamsException() {
        super();
    }

    public MipamsException(String message) {
        super(message);
        logger.log(Level.WARNING, message);
    }

    public MipamsException(String message, Throwable cause) {
        super(message, cause);
        logger.log(Level.WARNING, message, cause);
    }

    public MipamsException(Throwable cause) {
        super(cause);
        cause.printStackTrace();
        logger.log(Level.WARNING, cause.getMessage(), cause);
    }
}
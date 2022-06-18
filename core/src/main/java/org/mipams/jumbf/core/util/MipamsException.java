package org.mipams.jumbf.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MipamsException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(MipamsException.class);

    public MipamsException() {
        super();
    }

    public MipamsException(String message) {
        super(message);
        logger.error(message);
    }

    public MipamsException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);
    }

    public MipamsException(Throwable cause) {
        super(cause);
        cause.printStackTrace();
        logger.error(cause.getMessage(), cause);
    }
}
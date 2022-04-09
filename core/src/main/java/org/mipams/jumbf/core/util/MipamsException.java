package org.mipams.jumbf.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MipamsException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(MipamsException.class);

    public MipamsException() {
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
        logger.error(cause.getMessage(), cause);
    }
}
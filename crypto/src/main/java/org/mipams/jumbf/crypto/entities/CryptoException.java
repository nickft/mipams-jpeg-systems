package org.mipams.jumbf.crypto.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(CryptoException.class);

    public CryptoException(String message) {
        super(message);
        logger.error(message);
    }

    public CryptoException(String message, Throwable e) {
        super(message, e);
        logger.error(message, e);
    }

    public CryptoException(Throwable e) {
        super(e);
        logger.error("Failed to perform crypto operation: ", e);
    }

}

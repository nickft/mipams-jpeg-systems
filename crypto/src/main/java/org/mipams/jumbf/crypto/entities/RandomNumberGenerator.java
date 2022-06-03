package org.mipams.jumbf.crypto.entities;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    public byte[] getByteArray(int numOfBytes) throws CryptoException {

        if (numOfBytes > 256) {
            throw new CryptoException("Random Generator supports size up to 256 Bytes");
        }

        SecureRandom randomNumberGenerator = new SecureRandom();
        return randomNumberGenerator.generateSeed(numOfBytes);
    }

}

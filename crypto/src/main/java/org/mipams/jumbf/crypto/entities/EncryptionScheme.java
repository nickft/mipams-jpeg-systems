package org.mipams.jumbf.crypto.entities;

import org.mipams.jumbf.crypto.entities.request.CryptoRequest;

public interface EncryptionScheme {

    public String encrypt(CryptoRequest request) throws CryptoException;

    public String decrypt(CryptoRequest request) throws CryptoException;

}

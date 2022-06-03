package org.mipams.jumbf.crypto.entities;

import org.mipams.jumbf.crypto.entities.request.CryptoRequest;

public interface DigitalSignatureScheme {

    public byte[] sign(CryptoRequest request) throws CryptoException;

    public String verifySignature(CryptoRequest request) throws CryptoException;

}

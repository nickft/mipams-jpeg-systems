package org.mipams.jumbf.crypto.services;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.crypto.entities.CryptoException;
import org.mipams.jumbf.crypto.entities.DigitalSignatureScheme;
import org.mipams.jumbf.crypto.entities.EncryptionScheme;
import org.mipams.jumbf.crypto.entities.RandomNumberGenerator;
import org.mipams.jumbf.crypto.entities.ShaSignature;
import org.mipams.jumbf.crypto.entities.SymmetricEncryption;
import org.mipams.jumbf.crypto.entities.request.CryptoRequest;

import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    public String encryptDocument(SecretKey secretKey, CryptoRequest encryptionRequest) throws CryptoException {

        if (encryptionRequest.getContentFileUrl() == null) {
            throw new CryptoException("Content is not specified in the request");
        }

        EncryptionScheme encScheme = new SymmetricEncryption(secretKey);
        return encScheme.encrypt(encryptionRequest);
    }

    public String decryptDocument(SecretKey secretKey, CryptoRequest encryptionRequest) throws CryptoException {

        if (encryptionRequest.getContentFileUrl() == null) {
            throw new CryptoException("Content is not specified in the request");
        }

        EncryptionScheme encScheme = new SymmetricEncryption(secretKey);
        return encScheme.decrypt(encryptionRequest);
    }

    public byte[] signDocument(KeyPair credentials, CryptoRequest signRequest) throws CryptoException {

        if (signRequest.getContentFileUrl() == null) {
            throw new CryptoException("Content is not specified in the request");
        }

        DigitalSignatureScheme signScheme = new ShaSignature(credentials);
        return signScheme.sign(signRequest);
    }

    public String verifySignatureOfDocument(KeyPair credentials, CryptoRequest signRequest) throws CryptoException {

        if (signRequest.getContentFileUrl() == null) {
            throw new CryptoException("Content is not specified in the request");
        }

        if (signRequest.getSignatureHexEncoded() == null) {
            throw new CryptoException("Signature is not provided");
        }

        DigitalSignatureScheme signScheme = new ShaSignature(credentials);
        return signScheme.verifySignature(signRequest);
    }

    public byte[] getRandomNumber(int numOfBytes) throws CryptoException {
        RandomNumberGenerator rng = new RandomNumberGenerator();
        return rng.getByteArray(numOfBytes);
    }

    public boolean accessRulesVerifiedSuccessfully(String username, String roleList, JumbfBox accessRulesJumbfBox)
            throws CryptoException {
        return true;
    }
}

package org.mipams.jumbf.crypto.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.mipams.jumbf.crypto.entities.CryptoException;
import org.springframework.stereotype.Service;

@Service
public class KeyReaderService {

    public PrivateKey getPrivateKey(String fileUrl) throws CryptoException {

        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(fileUrl));

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (IOException e) {
            throw new CryptoException(e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    public PublicKey getPublicKey(String fileUrl) throws CryptoException {

        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(fileUrl));

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (IOException e) {
            throw new CryptoException(e);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }
}

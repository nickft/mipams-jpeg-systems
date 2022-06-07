package org.mipams.jumbf.crypto.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.mipams.jumbf.crypto.entities.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CredentialsReaderService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialsReaderService.class);

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

    public X509Certificate getCertificate(String fileUrl) throws CryptoException {
        try (FileInputStream fis = new FileInputStream(fileUrl);) {
            return getCertificate(fis);
        } catch (IOException e) {
            throw new CryptoException(e);
        }
    }

    public X509Certificate getCertificate(InputStream inputStream) throws CryptoException {
        try {
            Certificate cert = null;
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            while (inputStream.available() > 0) {
                cert = cf.generateCertificate(inputStream);
                logger.debug(cert.toString());
            }

            return (X509Certificate) cert;
        } catch (IOException | CertificateException e) {
            throw new CryptoException(e);
        }
    }

}

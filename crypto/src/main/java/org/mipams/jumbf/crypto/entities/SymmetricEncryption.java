package org.mipams.jumbf.crypto.entities;

import org.mipams.jumbf.crypto.entities.request.CryptoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import javax.xml.bind.DatatypeConverter;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SymmetricEncryption implements EncryptionScheme {

    private static final Logger logger = LoggerFactory.getLogger(SymmetricEncryption.class);

    private @Getter @Setter SecretKey symmetricKey;

    @Override
    public String encrypt(CryptoRequest request) throws CryptoException {

        logger.debug("Encryption request instantiated");

        Cipher cipher = getCipherInstance(Cipher.ENCRYPT_MODE, request.getIv());

        AlgorithmParameters algParams = cipher.getParameters();

        logger.info(algParams.toString());

        String outputFileName = request.getContentFileUrl() + ".enc";
        String outputFilePath = "/tmp/" + outputFileName;

        performCipherAndWriteToFileUsing(cipher, request.getContentFileUrl(), outputFilePath);

        logger.debug("Encryption request finished");

        return outputFilePath;
    }

    @Override
    public String decrypt(CryptoRequest request) throws CryptoException {

        logger.debug("Decryption request instantiated");

        Cipher cipher = getCipherInstance(Cipher.DECRYPT_MODE, request.getIv());

        String outputFileName = request.getContentFileUrl() + ".dec";
        String outputFilePath = "/tmp/" + outputFileName;

        performCipherAndWriteToFileUsing(cipher, request.getContentFileUrl(), outputFilePath);

        logger.debug("Decryption request finished");

        return outputFilePath;
    }

    private Cipher getCipherInstance(int opMode, String ivAsString) throws CryptoException {

        Cipher cipher;

        if (ivAsString == null) {
            throw new CryptoException("IV must be specified");
        }

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = DatatypeConverter.parseHexBinary(ivAsString);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("AES");

            algorithmParameters.init(ivSpec);

            cipher.init(opMode, getSymmetricKey(), algorithmParameters);
            return cipher;

        } catch (NoSuchPaddingException e) {
            throw new CryptoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        } catch (InvalidParameterSpecException e) {
            throw new CryptoException(e);
        }
    }

    private void performCipherAndWriteToFileUsing(Cipher cipher, @NonNull String inputFile,
            String outputFilePath) throws CryptoException {

        try (FileOutputStream fos = new FileOutputStream(outputFilePath);
                CipherInputStream cis = new CipherInputStream(new FileInputStream(inputFile), cipher);) {
            byte[] b = new byte[64];
            int i = cis.read(b);
            while (i != -1) {
                fos.write(b, 0, i);
                i = cis.read(b);
            }
        } catch (IOException e) {
            throw new CryptoException(e);
        }
    }

}

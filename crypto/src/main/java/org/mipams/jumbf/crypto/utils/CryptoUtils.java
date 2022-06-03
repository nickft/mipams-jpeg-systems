package org.mipams.jumbf.crypto.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mipams.jumbf.crypto.entities.CryptoException;
import org.mipams.jumbf.crypto.entities.request.CryptoRequest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class CryptoUtils {

    public static byte[] computeDigestOfRequest(CryptoRequest cryptoRequest) throws CryptoException {
        try (InputStream inputStream = new FileInputStream(cryptoRequest.getContentFileUrl())) {
            MessageDigest messageDigest = MessageDigest.getInstance(cryptoRequest.getCryptoMethod());

            while (inputStream.available() > 0) {
                byte[] buffer = new byte[64];

                inputStream.read(buffer);

                messageDigest.update(buffer);
            }

            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(cryptoRequest.getCryptoMethod() + " is not supported");
        } catch (IOException e) {
            throw new CryptoException("Fail to open file");
        }
    }

    public static ResponseEntity<?> createOctetResponse(String fileUrl) throws CryptoException {
        UrlResource urlResource;

        try {
            urlResource = new UrlResource("file:" + fileUrl);
        } catch (MalformedURLException e) {
            throw new CryptoException("The file path is malformed", e);
        }

        StringBuilder headerValue = new StringBuilder("attachment; filename=\"").append(fileUrl).append("\"");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue.toString())
                .body(urlResource);
    }
}

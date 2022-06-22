package org.mipams.jumbf.crypto.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.crypto.entities.CryptoException;
import org.mipams.jumbf.crypto.entities.DigitalSignatureScheme;
import org.mipams.jumbf.crypto.entities.EncryptionScheme;
import org.mipams.jumbf.crypto.entities.RandomNumberGenerator;
import org.mipams.jumbf.crypto.entities.ShaSignature;
import org.mipams.jumbf.crypto.entities.SymmetricEncryption;
import org.mipams.jumbf.crypto.entities.request.CryptoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);

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

    public String authorizeAccessToResource(JumbfBox accessRulesJumbfBox, String username, String roles)
            throws CryptoException {

        if (roles == null || roles.isEmpty()) {
            throw new CryptoException("No role was specified for this request");
        }

        logger.debug("Generating request");

        String requestInXACML = new BufferedReader(
                new InputStreamReader(CryptoService.class.getClassLoader().getResourceAsStream("request-template.xml"),
                        StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        logger.debug(requestInXACML);

        String role = getHigherRole(roles);
        requestInXACML = String.format(requestInXACML, role);

        XmlBox accessRulesXmlBox = (XmlBox) accessRulesJumbfBox.getContentBoxList().get(0);
        String policyInXACML = null;

        try (FileInputStream fis = new FileInputStream(accessRulesXmlBox.getFileUrl())) {
            logger.debug("Extracting policy from JUMBF Box");
            policyInXACML = new BufferedReader(new InputStreamReader(fis)).lines().collect(Collectors.joining("\n"));
            logger.debug(policyInXACML);
        } catch (IOException e) {
            throw new CryptoException(e);
        }

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://authorization-service:8080/polS/api/v1")
                .path("authorize_rule");

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_FORM_URLENCODED);

        Form form = new Form();
        form.param("request", requestInXACML);
        form.param("policy", policyInXACML);
        Response response = invocationBuilder.post(Entity.form(form));

        String responseBody = response.readEntity(String.class);

        if (responseBody.contains("<Decision>Permit</Decision>")) {
            return responseBody;
        }

        throw new CryptoException(responseBody);
    }

    private String getHigherRole(String roleList) {
        return roleList.contains("PRODUCER") ? "PRODUCER" : "CONSUMER";
    }

    public String generatePolicy(String... parameters) throws CryptoException {
        String policy_template = new BufferedReader(
                new InputStreamReader(CryptoService.class.getClassLoader().getResourceAsStream("policy-template.xml"),
                        StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));
        return policy_template;
    }
}

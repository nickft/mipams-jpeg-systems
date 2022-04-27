package org.mipams.jumbf.demo.services.privacy_security;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.core.BmffBoxParser;

import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox.MethodType;

import org.springframework.stereotype.Service;

@Service
public class ProtectionDescriptionBoxParser extends BmffBoxParser<ProtectionDescriptionBox> {

    @Override
    protected ProtectionDescriptionBox initializeBox() {
        return new ProtectionDescriptionBox();
    }

    @Override
    protected void populateBox(ProtectionDescriptionBox protectionDescriptionBox, ObjectNode input)
            throws MipamsException {

        JsonNode methodNode = input.get("method");

        if (methodNode == null) {
            throw new BadRequestException("Method must be specified");
        }

        MethodType method = MethodType.getMethodTypeFromString(methodNode.asText());

        switch (method) {
            case EXTERNAL:
                handleExternalMethod(protectionDescriptionBox, input);
                break;
            case AES_256_CBC:
                protectionDescriptionBox.setAes256CbcProtection();
                break;
            case AES_256_CBC_WITH_IV:
                handleAes256WithCbcWithIvMethod(protectionDescriptionBox, input);
                break;
            default:
        }

        JsonNode accessRulesNode = input.get("access-rules-label");

        if (accessRulesNode != null) {
            protectionDescriptionBox.setArLabel(accessRulesNode.asText());
            protectionDescriptionBox.includeAccessRulesInToggle();
        }
    }

    private void handleExternalMethod(ProtectionDescriptionBox protectionDescriptionBox, ObjectNode input) {
        String encryptionBoxLabel = input.get("external-label").asText();
        protectionDescriptionBox.setEncLabel(encryptionBoxLabel);
        protectionDescriptionBox.setProtectionMethodAsExternallyReferenced();
    }

    private void handleAes256WithCbcWithIvMethod(ProtectionDescriptionBox protectionDescriptionBox, ObjectNode input)
            throws BadRequestException {
        String ivAsHexString = input.get("ivHexString").asText();

        if (ivAsHexString.length() != 32) {
            throw new BadRequestException("IV needs to be 16 bytes");
        }

        byte[] iv = DatatypeConverter.parseHexBinary(ivAsHexString);

        protectionDescriptionBox.setIv(iv);
        protectionDescriptionBox.setAes256CbcWithIvProtection();
    }

}

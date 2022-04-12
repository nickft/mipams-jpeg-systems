package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.XtBoxService;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox.MethodType;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class ProtectionDescriptionBoxService extends XtBoxService<ProtectionDescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(ProtectionDescriptionBoxService.class);

    @Override
    protected ProtectionDescriptionBox initializeBox() throws MipamsException {
        return new ProtectionDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.ProtectionDescriptionBox.getServiceMetadata();
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

    @Override
    protected void writeXtBoxPayloadToJumbfFile(ProtectionDescriptionBox protectionDescriptionBox,
            FileOutputStream fileOutputStream)
            throws MipamsException {

        try {

            fileOutputStream
                    .write(CoreUtils.convertIntToSingleElementByteArray(protectionDescriptionBox.getMethodToggle()));

            if (protectionDescriptionBox.isProtectionExternallyReferenced()) {
                fileOutputStream
                        .write(CoreUtils
                                .convertStringToByteArray(protectionDescriptionBox.getEncLabelWithEscapeCharacter()));
            }

            if (protectionDescriptionBox.accessRulesExist()) {
                fileOutputStream.write(
                        CoreUtils.convertStringToByteArray(protectionDescriptionBox.getArLabelWithEscapeCharacter()));
            }

            if (protectionDescriptionBox.isAes256CbcWithIvProtection()) {
                fileOutputStream.write(protectionDescriptionBox.getIv());
            }

        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(ProtectionDescriptionBox protectionDescriptionBox, InputStream input)
            throws MipamsException {

        logger.debug("Protection Description box");

        long actualSize = 0;

        try {

            int methodToggle = CoreUtils.readSingleByteAsIntFromInputStream(input);
            actualSize++;

            protectionDescriptionBox.setMethodToggle(methodToggle);
            if (protectionDescriptionBox.isProtectionExternallyReferenced()) {
                String label = CoreUtils.readStringFromInputStream(input);

                // +1 for the null terminating character
                actualSize += CoreUtils.addEscapeCharacterToText(label).length();

                protectionDescriptionBox.setEncLabel(label);
            }

            if (protectionDescriptionBox.accessRulesExist()) {
                String label = CoreUtils.readStringFromInputStream(input);

                // +1 for the null terminating character
                actualSize += CoreUtils.addEscapeCharacterToText(label).length();

                protectionDescriptionBox.setArLabel(label);
            }

            if (protectionDescriptionBox.isAes256CbcWithIvProtection()) {
                byte[] iv = new byte[16];

                if (input.read(iv) == -1) {
                    throw new MipamsException();
                }

                protectionDescriptionBox.setIv(iv);
                actualSize += 16;
            }

        } catch (IOException e) {
            throw new CorruptedJumbfFileException(
                    "Failed to read description box after {" + Long.toString(actualSize) + "} bytes.", e);
        }

        verifyBoxSize(protectionDescriptionBox, actualSize);

        logger.debug("Discovered box: " + protectionDescriptionBox.toString());
    }
}

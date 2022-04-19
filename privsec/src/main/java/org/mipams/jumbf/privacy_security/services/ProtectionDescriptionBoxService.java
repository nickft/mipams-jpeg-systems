package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BmffBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class ProtectionDescriptionBoxService extends BmffBoxService<ProtectionDescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(ProtectionDescriptionBoxService.class);

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ProtectionDescriptionBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected ProtectionDescriptionBox initializeBox() {
        return new ProtectionDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(ProtectionDescriptionBox protectionDescriptionBox,
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
    protected void populatePayloadFromJumbfFile(ProtectionDescriptionBox protectionDescriptionBox,
            long availableBytesForBox, InputStream input)
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

        logger.debug("Discovered box: " + protectionDescriptionBox.toString());
    }
}

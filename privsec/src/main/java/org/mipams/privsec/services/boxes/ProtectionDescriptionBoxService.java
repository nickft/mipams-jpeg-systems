package org.mipams.privsec.services.boxes;

import java.io.OutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.boxes.BmffBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.privsec.entities.ProtectionDescriptionBox;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.stereotype.Service;

@Service
public class ProtectionDescriptionBoxService extends BmffBoxService<ProtectionDescriptionBox> {

    private static final Logger logger = Logger.getLogger(ProtectionDescriptionBoxService.class.getName());

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
            OutputStream outputStream)
            throws MipamsException {

        CoreUtils.writeIntAsSingleByteToOutputStream(protectionDescriptionBox.getMethodToggle(), outputStream);

        if (protectionDescriptionBox.isProtectionExternallyReferenced()) {
            final String encLabelWithEscapeCharacter = CoreUtils
                    .addEscapeCharacterToText(protectionDescriptionBox.getEncLabel());
            CoreUtils.writeTextToOutputStream(encLabelWithEscapeCharacter, outputStream);
        }

        if (protectionDescriptionBox.accessRulesExist()) {
            final String arLabelWithEscapeCharacter = CoreUtils
                    .addEscapeCharacterToText(protectionDescriptionBox.getArLabel());
            CoreUtils.writeTextToOutputStream(arLabelWithEscapeCharacter, outputStream);
        }

        if (protectionDescriptionBox.isAes256CbcWithIvProtection()) {
            CoreUtils.writeByteArrayToOutputStream(protectionDescriptionBox.getIv(), outputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(ProtectionDescriptionBox protectionDescriptionBox,
            ParseMetadata parseMetadata, InputStream input) throws MipamsException {

        logger.log(Level.FINE, "Protection Description box");

        int methodToggle = CoreUtils.readSingleByteAsIntFromInputStream(input);
        protectionDescriptionBox.setMethodToggle(methodToggle);

        if (protectionDescriptionBox.isProtectionExternallyReferenced()) {
            String label = CoreUtils.readStringFromInputStream(input);
            protectionDescriptionBox.setEncLabel(label);
        }

        if (protectionDescriptionBox.accessRulesExist()) {
            String label = CoreUtils.readStringFromInputStream(input);
            protectionDescriptionBox.setArLabel(label);
        }

        if (protectionDescriptionBox.isAes256CbcWithIvProtection()) {
            int ivSize = 16;
            byte[] iv = CoreUtils.readBytesFromInputStream(input, ivSize);

            protectionDescriptionBox.setIv(iv);
        }

        logger.log(Level.FINE, "Discovered box: " + protectionDescriptionBox.toString());
    }
}

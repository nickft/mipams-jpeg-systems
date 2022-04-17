package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public final class DescriptionBoxService extends BmffBoxService<DescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(DescriptionBoxService.class);

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.DescriptionBox.getServiceMetadata();
    }

    @Override
    protected DescriptionBox initializeBox() throws MipamsException {
        return new DescriptionBox();
    }

    @Override
    protected void populateBox(DescriptionBox descriptionBox, ObjectNode input) throws MipamsException {

        String type = input.get("contentType").asText();

        ServiceMetadata serviceMetadata = contentBoxDiscoveryManager.getMetadataForContentBoxServiceWithType(type);

        if (serviceMetadata == null) {
            throw new BadRequestException("Content Type: " + type + " is not supported");
        }

        descriptionBox.setUuid(serviceMetadata.getContentTypeUuid());

        JsonNode node = input.get("requestable");
        int toggle = (node == null || !node.asBoolean()) ? 0 : 1;

        node = input.get("label");
        if (node != null) {
            descriptionBox.setLabel(node.asText());
            toggle = toggle | 2;
        }

        node = input.get("id");
        if (node != null) {
            descriptionBox.setId(node.asInt());
            toggle = toggle | 4;
        }

        descriptionBox.setToggle(toggle);

        node = input.get("sha256Hash");
        if (node != null) {
            byte[] sha256Hash = DatatypeConverter.parseHexBinary(node.asText());
            descriptionBox.setSha256Hash(sha256Hash);

            toggle = toggle | 8;
        }
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(DescriptionBox descriptionBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        try {
            fileOutputStream.write(CoreUtils.convertUUIDToByteArray(descriptionBox.getUuid()));
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(descriptionBox.getToggle()));

            if (descriptionBox.labelExists()) {
                fileOutputStream
                        .write(CoreUtils.convertStringToByteArray(descriptionBox.getLabelWithEscapeCharacter()));
            }

            if (descriptionBox.idExists()) {
                fileOutputStream.write(CoreUtils.convertIntToByteArray(descriptionBox.getId()));
            }

            if (descriptionBox.signatureExists()) {
                fileOutputStream.write(descriptionBox.getSha256Hash());
            }

        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(DescriptionBox descriptionBox, long availableBytesForBox,
            InputStream input) throws MipamsException {

        logger.debug("Description box");

        long actualSize = 0;

        try {
            UUID uuidVal = CoreUtils.readUuidFromInputStream(input);
            descriptionBox.setUuid(uuidVal);

            actualSize += CoreUtils.UUID_BYTE_SIZE;

            int toggleValue = CoreUtils.readSingleByteAsIntFromInputStream(input);
            descriptionBox.setToggle(toggleValue);
            actualSize++;

            if (descriptionBox.labelExists()) {

                String label = CoreUtils.readStringFromInputStream(input);

                // +1 for the null terminating character
                actualSize += CoreUtils.addEscapeCharacterToText(label).length();

                descriptionBox.setLabel(label);
            }

            if (descriptionBox.idExists()) {

                byte[] temp = new byte[4];

                if (input.read(temp, 0, 4) == -1) {
                    throw new MipamsException();
                }

                int idVal = CoreUtils.convertByteArrayToInt(temp);
                descriptionBox.setId(idVal);
                actualSize += 4;
            }

            if (descriptionBox.signatureExists()) {
                byte[] sha256Hash = new byte[32];

                if (input.read(sha256Hash, 0, 32) == -1) {
                    throw new MipamsException();
                }

                descriptionBox.setSha256Hash(sha256Hash);
                actualSize += 32;
            }
        } catch (IOException e) {
            throw new CorruptedJumbfFileException(
                    "Failed to read description box after {" + Long.toString(actualSize) + "} bytes.", e);
        }

        verifyBoxSize(descriptionBox, actualSize);

        logger.debug("Discovered box: " + descriptionBox.toString());
    }

}
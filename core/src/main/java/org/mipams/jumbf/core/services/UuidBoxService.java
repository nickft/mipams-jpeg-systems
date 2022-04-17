package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UuidBoxService extends BmffBoxService<UuidBox> implements ContentBoxService<UuidBox> {

    private static final Logger logger = LoggerFactory.getLogger(UuidBoxService.class);

    @Autowired
    Properties properties;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.UuidBox.getServiceMetadata();
    }

    @Override
    protected void populateBox(UuidBox uuidBox, ObjectNode input) throws MipamsException {
        String uuidAsString = input.get("uuid").asText();

        try {
            uuidBox.setUuid(UUID.fromString(uuidAsString));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID format: ", e);

        }

        String path = input.get("fileUrl").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        uuidBox.setFileUrl(path);
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(UuidBox uuidBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        try {
            fileOutputStream.write(CoreUtils.convertUUIDToByteArray(uuidBox.getUuid()));

            properties.checkIfFileSizeExceedApplicationLimits(uuidBox.getFileUrl());
            CoreUtils.writeFileContentToOutput(uuidBox.getFileUrl(), fileOutputStream);

        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }

    }

    @Override
    protected UuidBox initializeBox() throws MipamsException {
        return new UuidBox();
    }

    @Override
    protected void populatePayloadFromJumbfFile(UuidBox uuidBox, long availableBytesForBox, InputStream input)
            throws MipamsException {
        logger.debug("UUID box");

        long actualSize = 0;

        try {

            UUID uuidVal = CoreUtils.readUuidFromInputStream(input);
            uuidBox.setUuid(uuidVal);

            actualSize += CoreUtils.UUID_BYTE_SIZE;

            String fileName = CoreUtils.randomStringGenerator();
            String fullPath = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);

            uuidBox.setFileUrl(fullPath);

            long nominalTotalSizeInBytes = uuidBox.getPayloadSizeFromBmffHeaders();
            CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, uuidBox.getFileUrl());

            actualSize += CoreUtils.getFileSizeFromPath(uuidBox.getFileUrl());
        } catch (MipamsException e) {
            throw new CorruptedJumbfFileException(
                    "Failed to read UUID box after {" + Long.toString(actualSize) + "} bytes.", e);
        }

        verifyBoxSize(uuidBox, actualSize);
        logger.debug("Discovered box: " + uuidBox.toString());
    }

}

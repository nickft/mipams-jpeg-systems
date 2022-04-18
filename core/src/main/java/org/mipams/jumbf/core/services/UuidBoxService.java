package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.util.BadRequestException;
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

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        UuidBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    protected UuidBox initializeBox() {
        return new UuidBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void populateBox(UuidBox uuidBox, ObjectNode input) throws MipamsException {
        String uuid = input.get("uuid").asText();

        try {
            uuidBox.setUuid(uuid);
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

        CoreUtils.writeUuidToOutputStream(uuidBox.getUuid(), fileOutputStream);

        properties.checkIfFileSizeExceedApplicationLimits(uuidBox.getFileUrl());
        CoreUtils.writeFileContentToOutput(uuidBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(UuidBox uuidBox, long availableBytesForBox, InputStream input)
            throws MipamsException {
        logger.debug("UUID box");

        long nominalTotalSizeInBytes = availableBytesForBox;

        try {

            String uuid = CoreUtils.readUuidFromInputStream(input);
            uuidBox.setUuid(uuid);
            nominalTotalSizeInBytes -= CoreUtils.UUID_BYTE_SIZE;

            String fileName = CoreUtils.randomStringGenerator();
            String fullPath = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);
            uuidBox.setFileUrl(fullPath);

            CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, uuidBox.getFileUrl());
        } catch (MipamsException e) {
            throw new CorruptedJumbfFileException("Failed to read UUID box", e);
        }

        logger.debug("Discovered box: " + uuidBox.toString());
    }

}

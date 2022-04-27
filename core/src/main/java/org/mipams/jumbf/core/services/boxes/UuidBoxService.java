package org.mipams.jumbf.core.services.boxes;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UuidBoxService extends BmffBoxService<UuidBox> {

    private static final Logger logger = LoggerFactory.getLogger(UuidBoxService.class);

    @Autowired
    Properties properties;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        UuidBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
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

        String uuid = CoreUtils.readUuidFromInputStream(input);
        uuidBox.setUuid(uuid);
        nominalTotalSizeInBytes -= CoreUtils.UUID_BYTE_SIZE;

        String fileName = CoreUtils.randomStringGenerator();
        String fullPath = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);
        uuidBox.setFileUrl(fullPath);

        CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, uuidBox.getFileUrl());

        logger.debug("Discovered box: " + uuidBox.toString());
    }

}

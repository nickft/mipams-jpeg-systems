package org.mipams.jumbf.services.boxes;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.OutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.entities.UuidBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class UuidBoxService extends BmffBoxService<UuidBox> {

    private static final Logger logger = Logger.getLogger(UuidBoxService.class.getName());

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
    protected void writeBmffPayloadToJumbfFile(UuidBox uuidBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeUuidToOutputStream(uuidBox.getUuid(), outputStream);
        CoreUtils.writeFileContentToOutput(uuidBox.getFileUrl(), outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(UuidBox uuidBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {
        logger.log(Level.FINE,"UUID box");

        long nominalTotalSizeInBytes = parseMetadata.getAvailableBytesForBox();

        String uuid = CoreUtils.readUuidFromInputStream(input);
        uuidBox.setUuid(uuid);
        nominalTotalSizeInBytes -= CoreUtils.UUID_BYTE_SIZE;

        String fileName = CoreUtils.randomStringGenerator();
        String fullPath = CoreUtils.getFullPath(parseMetadata.getParentDirectory(), fileName);
        uuidBox.setFileUrl(fullPath);

        CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, uuidBox.getFileUrl());

        logger.log(Level.FINE,"Discovered box: " + uuidBox.toString());
    }

}

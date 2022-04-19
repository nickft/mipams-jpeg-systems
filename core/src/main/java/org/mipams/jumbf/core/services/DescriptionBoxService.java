package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public final class DescriptionBoxService extends BmffBoxService<DescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(DescriptionBoxService.class);

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        DescriptionBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected DescriptionBox initializeBox() {
        return new DescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(DescriptionBox descriptionBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        CoreUtils.writeUuidToOutputStream(descriptionBox.getUuid(), fileOutputStream);
        CoreUtils.writeIntAsSingleByteToOutputStream(descriptionBox.getToggle(), fileOutputStream);

        if (descriptionBox.labelExists()) {
            CoreUtils.writeTextToOutputStream(descriptionBox.getLabelWithEscapeCharacter(), fileOutputStream);
        }

        if (descriptionBox.idExists()) {
            CoreUtils.writeIntToOutputStream(descriptionBox.getId(), fileOutputStream);
        }

        if (descriptionBox.sha256HashExists()) {
            CoreUtils.writeByteArrayToOutputStream(descriptionBox.getSha256Hash(), fileOutputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(DescriptionBox descriptionBox, long availableBytesForBox,
            InputStream input) throws MipamsException {

        logger.debug("Description box");

        String uuid = CoreUtils.readUuidFromInputStream(input);
        descriptionBox.setUuid(uuid);

        int toggleValue = CoreUtils.readSingleByteAsIntFromInputStream(input);
        descriptionBox.setToggle(toggleValue);

        if (descriptionBox.labelExists()) {
            String label = CoreUtils.readStringFromInputStream(input);

            descriptionBox.setLabel(label);
        }

        if (descriptionBox.idExists()) {
            int idVal = CoreUtils.readIntFromInputStream(input);
            descriptionBox.setId(idVal);
        }

        if (descriptionBox.sha256HashExists()) {
            byte[] sha256Hash = CoreUtils.readBytesFromInputStream(input, 32);
            descriptionBox.setSha256Hash(sha256Hash);
        }

        logger.debug("Discovered box: " + descriptionBox.toString());
    }

}
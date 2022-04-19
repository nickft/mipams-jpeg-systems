package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public final class PaddingBoxService extends BmffBoxService<PaddingBox> {

    private static final Logger logger = LoggerFactory.getLogger(PaddingBoxService.class);

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        PaddingBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected PaddingBox initializeBox() {
        return new PaddingBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(PaddingBox paddingBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        CoreUtils.writePaddingToOutputStream(paddingBox.getPaddingSize(), PaddingBox.PADDING_VALUE,
                fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(PaddingBox paddingBox, long availableBytesForBox, InputStream input)
            throws MipamsException {

        logger.debug("Padding box");

        long actualSize = CoreUtils.parsePaddingFromInputStream(input, PaddingBox.PADDING_VALUE, availableBytesForBox);
        paddingBox.setPaddingSize(actualSize);

        logger.debug("Discovered box: " + paddingBox.toString());
    }

}
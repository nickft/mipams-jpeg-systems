package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public final class PaddingBoxService extends BmffBoxService<PaddingBox> {

    private static final Logger logger = LoggerFactory.getLogger(PaddingBoxService.class);

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.PaddingBox.getServiceMetadata();
    }

    @Override
    protected PaddingBox initializeBox() throws MipamsException {
        return new PaddingBox();
    }

    @Override
    protected void populateBox(PaddingBox paddingBox, ObjectNode input) throws MipamsException {
        long payloadSize = input.get("size").asLong();
        paddingBox.setPaddingSize(payloadSize);

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
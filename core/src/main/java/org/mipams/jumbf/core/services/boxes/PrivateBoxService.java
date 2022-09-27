package org.mipams.jumbf.core.services.boxes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.PrivateBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrivateBoxService extends BmffBoxService<PrivateBox> {

    private static final Logger logger = LoggerFactory.getLogger(PrivateBoxService.class);

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Autowired
    JumbfBoxService jumbfBoxService;

    @Autowired
    PaddingBoxService paddingBoxService;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        PrivateBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected PrivateBox initializeBox() {
        return new PrivateBox();
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(PrivateBox privateBox, OutputStream outputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(privateBox.getDescriptionBox(), outputStream);

        for (BmffBox box : privateBox.getContentBoxList()) {
            JumbfBox jumbfBox = (JumbfBox) box;
            jumbfBoxService.writeToJumbfFile(jumbfBox, outputStream);
        }

        if (privateBox.getPaddingBox() != null) {
            paddingBoxService.writeToJumbfFile(privateBox.getPaddingBox(), outputStream);
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(PrivateBox jumbfBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        logger.debug("Jumbf box");

        final long nominalPayloadSize = jumbfBox.getPayloadSizeFromBmffHeaders();

        ParseMetadata descriptionParseMetadata = new ParseMetadata();
        descriptionParseMetadata.setAvailableBytesForBox(nominalPayloadSize);
        descriptionParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

        jumbfBox.setDescriptionBox(descriptionBoxService.parseFromJumbfFile(input, descriptionParseMetadata));

        long actualSize = jumbfBox.getDescriptionBox().getBoxSizeFromBmffHeaders();

        try {

            while ((nominalPayloadSize == 0 && input.available() > 0) || (actualSize < nominalPayloadSize)) {

                parseMetadata = new ParseMetadata();
                parseMetadata.setAvailableBytesForBox(nominalPayloadSize != 0 ? nominalPayloadSize - actualSize : 0);
                parseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

                if (CoreUtils.isPaddingBoxNext(input)) {
                    PaddingBox paddingBox = paddingBoxService.parseFromJumbfFile(input, parseMetadata);
                    jumbfBox.setPaddingBox(paddingBox);
                    break;
                }

                JumbfBox contentBox = jumbfBoxService.parseFromJumbfFile(input, parseMetadata);
                jumbfBox.getContentBoxList().add(contentBox);

                actualSize += jumbfBox.calculateSizeFromBox();
            }

        } catch (IOException e) {
            throw new MipamsException(e);
        }

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

}

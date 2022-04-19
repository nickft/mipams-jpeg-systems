package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class JumbfBoxService extends BmffBoxService<JumbfBox> implements ContentBoxService<JumbfBox> {

    private static final Logger logger = LoggerFactory.getLogger(JumbfBoxService.class);

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    @Autowired
    PaddingBoxService paddingBoxService;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        JumbfBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    protected JumbfBox initializeBox() {
        return new JumbfBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        ContentBoxService contentBoxService = contentBoxDiscoveryManager
                .getContentBoxServiceBasedOnBoxWithId(jumbfBox.getContentBox().getTypeId());
        contentBoxService.writeToJumbfFile(jumbfBox.getContentBox(), fileOutputStream);

        if (jumbfBox.getPaddingBox() != null) {
            paddingBoxService.writeToJumbfFile(jumbfBox.getPaddingBox(), fileOutputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, long availableBytesForBox, InputStream input)
            throws MipamsException {

        logger.debug("Jumbf box");

        final long nominalPayloadSize = jumbfBox.getPayloadSizeFromBmffHeaders();

        jumbfBox.setDescriptionBox(descriptionBoxService.parseFromJumbfFile(input, nominalPayloadSize));

        long actualSize = jumbfBox.getDescriptionBox().getBoxSizeFromBmffHeaders();

        ContentBoxService contentBoxService = contentBoxDiscoveryManager
                .getContentBoxServiceBasedOnContentUUID(jumbfBox.getDescriptionBox().getUuid());

        ContentBox contentBox = contentBoxService.parseFromJumbfFile(input, nominalPayloadSize - actualSize);

        actualSize += contentBox.getBoxSize();

        jumbfBox.setContentBox(contentBox);

        if (!actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(jumbfBox)) {
            PaddingBox paddingBox = paddingBoxService.parseFromJumbfFile(input, nominalPayloadSize - actualSize);
            jumbfBox.setPaddingBox(paddingBox);
        }

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    public JumbfBox parseSuperBox(InputStream input) throws MipamsException {
        return super.parseFromJumbfFile(input, -1);
    }
}
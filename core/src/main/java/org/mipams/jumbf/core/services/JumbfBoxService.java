package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BoxTypeEnum;

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

    @Override
    protected JumbfBox initializeBox() throws MipamsException {
        return new JumbfBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.JumbfBox.getServiceMetadata();
    }

    @Override
    protected void populateBox(JumbfBox jumbfBox, ObjectNode input) throws MipamsException {

        ObjectNode descriptionNode = (ObjectNode) input.get("description");

        jumbfBox.setDescriptionBox(descriptionBoxService.discoverBoxFromRequest(descriptionNode));

        ObjectNode contentNode = (ObjectNode) input.get("content");

        UUID contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentBoxService contentBoxService = contentBoxDiscoveryManager
                .getContentBoxServiceBasedOnContentUUID(contentTypeUuid);

        ContentBox contentBox = contentBoxService.discoverBoxFromRequest(contentNode);

        jumbfBox.setContentBox(contentBox);

        if (input.has("padding")) {
            ObjectNode paddingNode = (ObjectNode) input.get("padding");

            PaddingBox paddingBox = paddingBoxService.discoverBoxFromRequest(paddingNode);
            jumbfBox.setPaddingBox(paddingBox);
        }
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
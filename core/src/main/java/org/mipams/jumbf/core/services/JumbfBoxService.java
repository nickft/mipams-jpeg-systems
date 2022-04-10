package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BoxTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class JumbfBoxService extends XtBoxService<JumbfBox> implements ContentBoxService<JumbfBox> {

    private static final Logger logger = LoggerFactory.getLogger(JumbfBoxService.class);

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

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
    }

    @Override
    protected void writeXtBoxPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        ContentBoxService contentBoxService = contentBoxDiscoveryManager
                .generateContentBoxServiceBasedOnBoxWithId(jumbfBox.getContentBox().getTypeId());
        contentBoxService.writeToJumbfFile(jumbfBox.getContentBox(), fileOutputStream);

    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, InputStream input) throws MipamsException {

        logger.debug("Jumbf box");

        final long nominalPayloadSize = jumbfBox.getPayloadSizeFromXTBoxHeaders();
        long actualSize = 0;

        jumbfBox.setDescriptionBox(descriptionBoxService.parseFromJumbfFile(input, nominalPayloadSize));

        actualSize += jumbfBox.getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        ContentBoxService contentBoxService = contentBoxDiscoveryManager
                .getContentBoxServiceBasedOnContentUUID(jumbfBox.getDescriptionBox().getUuid());

        ContentBox contentBox = contentBoxService.parseFromJumbfFile(input, nominalPayloadSize - actualSize);

        actualSize += contentBox.getBoxSize();

        jumbfBox.setContentBox(contentBox);

        verifyBoxSize(jumbfBox, actualSize);

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    public JumbfBox parseSuperBox(InputStream input) throws MipamsException {
        return super.parseFromJumbfFile(input, -1);
    }
}
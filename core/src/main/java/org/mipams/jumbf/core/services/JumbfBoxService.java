package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
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
    protected void populateBox(JumbfBox jumbfBox, ObjectNode input) throws MipamsException {

        ObjectNode descriptionNode = (ObjectNode) input.get("description");

        jumbfBox.setDescriptionBox(descriptionBoxService.discoverBoxFromRequest(descriptionNode));

        JsonNode contentNodeList = input.get("contentList");

        Iterator<JsonNode> contentIterator = contentNodeList.elements();
        List<ContentBox> contentList = new ArrayList<>();

        while (contentIterator.hasNext()) {

            ObjectNode contentNode = (ObjectNode) contentIterator.next();

            UUID contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

            ContentBoxService contentBoxService = contentBoxDiscoveryManager
                    .getContentBoxServiceBasedOnContentUUID(contentTypeUuid);

            ContentBox contentBoxContent = contentBoxService.discoverBoxFromRequest(contentNode);

            contentList.add(contentBoxContent);
        }

        jumbfBox.setContentList(contentList);
    }

    @Override
    protected void writeXtBoxPayloadToJumbfFile(JumbfBox jumbfBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        descriptionBoxService.writeToJumbfFile(jumbfBox.getDescriptionBox(), fileOutputStream);

        for (ContentBox contentBox : jumbfBox.getContentList()) {

            ContentBoxService xtBoxService = contentBoxDiscoveryManager
                    .generateContentBoxServiceBasedOnBoxWithId(contentBox.getTypeId());
            xtBoxService.writeToJumbfFile(contentBox, fileOutputStream);

        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(JumbfBox jumbfBox, InputStream input) throws MipamsException {

        logger.debug("Jumbf box");

        long actualSize = 0;

        jumbfBox.setDescriptionBox((DescriptionBox) descriptionBoxService.parseFromJumbfFile(input));

        actualSize += jumbfBox.getDescriptionBox().getBoxSizeFromXTBoxHeaders();

        List<ContentBox> contentList = new ArrayList<>();

        do {
            ContentBoxService contentBoxService = contentBoxDiscoveryManager
                    .getContentBoxServiceBasedOnContentUUID(jumbfBox.getDescriptionBox().getUuid());

            ContentBox contentBox = contentBoxService.parseFromJumbfFile(input);

            actualSize += contentBox.calculateSizeFromBox();

            contentList.add(contentBox);
        } while (actualSize < jumbfBox.getPayloadSizeFromXTBoxHeaders());

        jumbfBox.setContentList(contentList);

        verifyBoxSize(jumbfBox, actualSize);

        logger.debug("Discovered box: " + jumbfBox.toString());
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.JumbfBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.JumbfBox.getType();
    }

}
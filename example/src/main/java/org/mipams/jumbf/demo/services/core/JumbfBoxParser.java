package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.JumbfBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.ContentBoxParserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JumbfBoxParser extends BmffBoxParser<JumbfBox> implements ContentBoxParser {

    @Autowired
    DescriptionBoxParser descriptionBoxParser;

    @Autowired
    JumbfBoxService jumbfBoxService;

    @Autowired
    ContentBoxParserManager contentBoxParserManager;

    @Autowired
    PaddingBoxParser paddingBoxParser;

    @Override
    protected JumbfBox initializeBox() {
        return new JumbfBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return jumbfBoxService.getServiceMetadata();
    }

    @Override
    protected void populateBox(JumbfBox jumbfBox, ObjectNode input) throws MipamsException {

        ObjectNode descriptionNode = (ObjectNode) input.get("description");

        jumbfBox.setDescriptionBox(descriptionBoxParser.discoverBoxFromRequest(descriptionNode));

        ObjectNode contentNode = (ObjectNode) input.get("content");

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentBoxParser contentBoxParser = contentBoxParserManager
                .getParserBasedOnContentUUID(contentTypeUuid);

        ContentBox contentBox = contentBoxParser.discoverBoxFromRequest(contentNode);

        jumbfBox.setContentBox(contentBox);

        if (input.has("padding")) {
            ObjectNode paddingNode = (ObjectNode) input.get("padding");

            PaddingBox paddingBox = paddingBoxParser.discoverBoxFromRequest(paddingNode);
            jumbfBox.setPaddingBox(paddingBox);
        }
    }
}

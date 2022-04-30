package org.mipams.jumbf.demo.services.core;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.ContentTypeParserManager;
import org.mipams.jumbf.demo.services.ContentTypeParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JumbfBoxParser extends BmffBoxParser<JumbfBox> {

    @Autowired
    DescriptionBoxParser descriptionBoxParser;

    @Autowired
    ContentTypeParserManager contentTypeParserManager;

    @Autowired
    PaddingBoxParser paddingBoxParser;

    @Override
    protected JumbfBox initializeBox() {
        return new JumbfBox();
    }

    @Override
    protected void populateBox(JumbfBox jumbfBox, ObjectNode input) throws MipamsException {

        ObjectNode descriptionNode = (ObjectNode) input.get("description");

        jumbfBox.setDescriptionBox(descriptionBoxParser.discoverBoxFromRequest(descriptionNode));

        ObjectNode contentNode = (ObjectNode) input.get("content");

        String contentTypeUuid = jumbfBox.getDescriptionBox().getUuid();

        ContentTypeParser contentTypeParser = contentTypeParserManager
                .getParserBasedOnContentUUID(contentTypeUuid);

        List<BmffBox> contentBoxList = contentTypeParser.discoverContentBoxesFromRequest(contentNode);

        jumbfBox.setContentBoxList(contentBoxList);

        if (input.has("padding")) {
            ObjectNode paddingNode = (ObjectNode) input.get("padding");

            PaddingBox paddingBox = paddingBoxParser.discoverBoxFromRequest(paddingNode);
            jumbfBox.setPaddingBox(paddingBox);
        }
    }
}

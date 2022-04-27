package org.mipams.jumbf.demo.services.core;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.services.boxes.DescriptionBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.ContentTypeParserManager;
import org.mipams.jumbf.demo.services.ContentTypeParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionBoxParser extends BmffBoxParser<DescriptionBox> {

    @Autowired
    ContentTypeParserManager contentTypeParserManager;

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Override
    protected void populateBox(DescriptionBox descriptionBox, ObjectNode input) throws MipamsException {

        String uuid = input.get("contentTypeUuid").asText();

        ContentTypeParser contentTypeParser = contentTypeParserManager.getParserBasedOnContentUUID(uuid);
        descriptionBox.setUuid(contentTypeParser.getContentTypeUuid());

        JsonNode node = input.get("requestable");

        node = input.get("label");
        if (node != null) {
            descriptionBox.setLabel(node.asText());
        }

        node = input.get("id");
        if (node != null) {
            descriptionBox.setId(node.asInt());
        }

        node = input.get("sha256Hash");
        if (node != null) {
            byte[] sha256Hash = DatatypeConverter.parseHexBinary(node.asText());
            descriptionBox.setSha256Hash(sha256Hash);
        }

        descriptionBox.computeAndSetToggleBasedOnFields();
    }

    @Override
    protected DescriptionBox initializeBox() {
        return new DescriptionBox();
    }
}

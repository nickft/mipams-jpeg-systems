package org.mipams.jumbf.demo.services.core;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.core.services.DescriptionBoxService;

@Service
public class DescriptionBoxParser extends BmffBoxParser<DescriptionBox> {

    @Autowired
    ContentBoxDiscoveryManager contentBoxDiscoveryManager;

    @Autowired
    DescriptionBoxService descriptionBoxService;

    @Override
    protected void populateBox(DescriptionBox descriptionBox, ObjectNode input) throws MipamsException {

        String type = input.get("contentType").asText();

        ServiceMetadata serviceMetadata = contentBoxDiscoveryManager.getMetadataForContentBoxServiceWithType(type);

        if (serviceMetadata == null) {
            throw new BadRequestException("Content Type: " + type + " is not supported");
        }

        descriptionBox.setUuid(serviceMetadata.getContentTypeUuid());

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

    @Override
    public ServiceMetadata getServiceMetadata() {
        return descriptionBoxService.getServiceMetadata();
    }

}

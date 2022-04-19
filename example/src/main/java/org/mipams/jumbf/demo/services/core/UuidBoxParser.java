package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.services.UuidBoxService;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class UuidBoxParser extends BmffBoxParser<UuidBox> implements ContentBoxParser {

    @Autowired
    UuidBoxService uuidBoxService;

    @Override
    protected void populateBox(UuidBox uuidBox, ObjectNode input) throws MipamsException {
        String uuid = input.get("uuid").asText();

        try {
            uuidBox.setUuid(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID format: ", e);

        }

        String path = input.get("fileUrl").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        uuidBox.setFileUrl(path);
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return uuidBoxService.getServiceMetadata();
    }

    @Override
    protected UuidBox initializeBox() {
        return new UuidBox();
    }

}

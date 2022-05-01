package org.mipams.jumbf.demo.services.core;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.services.content_types.UuidContentType;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.ContentTypeParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UuidBoxParser extends BmffBoxParser<UuidBox> implements ContentTypeParser {

    @Autowired
    UuidContentType uuidContentType;

    @Value("${org.mipams.core.image_folder}")
    String ASSET_DIRECTORY;

    @Override
    protected void populateBox(UuidBox uuidBox, ObjectNode input) throws MipamsException {
        String uuid = input.get("uuid").asText();

        try {
            uuidBox.setUuid(uuid);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID format: ", e);

        }

        String fileName = input.get("fileName").asText();

        if (fileName == null) {
            throw new BadRequestException("Path is not specified");
        }

        String path = CoreUtils.getFullPath(ASSET_DIRECTORY, fileName);

        uuidBox.setFileUrl(path);

        uuidBox.setFileUrl(path);
    }

    @Override
    protected UuidBox initializeBox() {
        return new UuidBox();
    }

    @Override
    public List<BmffBox> discoverContentBoxesFromRequest(ObjectNode input) throws MipamsException {
        return List.of(discoverBoxFromRequest(input));
    }

    @Override
    public String getContentTypeUuid() {
        return uuidContentType.getContentTypeUuid();
    }

}

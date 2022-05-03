package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.SingleFormatBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Value;

public abstract class SingleFormatParser<T extends SingleFormatBox> extends BmffBoxParser<T> {

    @Value("${org.mipams.core.image_folder}")
    String ASSET_DIRECTORY;

    @Override
    protected void populateBox(T singleFormatBox, ObjectNode input) throws MipamsException {

        if (input == null) {
            throw new BadRequestException(
                    "Expected input for box type " + singleFormatBox.getType() + " but nothing found");
        }

        String fileName = input.get("fileName").asText();

        if (fileName == null) {
            throw new BadRequestException("Path is not specified");
        }

        String path = CoreUtils.getFullPath(ASSET_DIRECTORY, fileName);

        singleFormatBox.setFileUrl(path);
    }
}

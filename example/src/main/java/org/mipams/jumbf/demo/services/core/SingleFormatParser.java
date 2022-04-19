package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.SingleFormatBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;

public abstract class SingleFormatParser<T extends SingleFormatBox> extends BmffBoxParser<T> {
    @Override
    protected void populateBox(T singleFormatBox, ObjectNode input) throws MipamsException {
        String path = input.get("fileUrl").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        singleFormatBox.setFileUrl(path);
    }
}

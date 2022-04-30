package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileDescriptionBoxParser extends BmffBoxParser<EmbeddedFileDescriptionBox> {

    @Override
    protected EmbeddedFileDescriptionBox initializeBox() {
        return new EmbeddedFileDescriptionBox();
    }

    @Override
    protected void populateBox(EmbeddedFileDescriptionBox embeddedFileDescriptionBox, ObjectNode input)
            throws MipamsException {

        try {
            embeddedFileDescriptionBox.setMediaTypeFromString(input.get("mediaType").asText());
        } catch (MipamsException e) {
            throw new BadRequestException(e);
        } catch (NullPointerException e) {
            throw new BadRequestException("Media type not specified", e);
        }

        JsonNode node = input.get("fileName");

        if (node != null) {
            embeddedFileDescriptionBox.setFileName(node.asText());
        }

        node = input.get("fileExternallyReferenced");

        if (node != null) {
            if (node.asBoolean()) {
                embeddedFileDescriptionBox.markFileAsExternallyReferenced();
            } else {
                embeddedFileDescriptionBox.markFileAsInternallyReferenced();
            }
        } else {
            embeddedFileDescriptionBox.markFileAsExternallyReferenced();
        }

        embeddedFileDescriptionBox.computeAndSetToggleBasedOnFields();
    }

}

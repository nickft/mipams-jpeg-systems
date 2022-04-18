package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileDescriptionBoxService extends BmffBoxService<EmbeddedFileDescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileDescriptionBoxService.class);

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        EmbeddedFileDescriptionBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected EmbeddedFileDescriptionBox initializeBox() {
        return new EmbeddedFileDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
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

    @Override
    protected void writeBmffPayloadToJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            FileOutputStream fileOutputStream) throws MipamsException {

        CoreUtils.writeIntAsSingleByteToOutputStream(embeddedFileDescriptionBox.getToggle(), fileOutputStream);

        CoreUtils.writeTextToOutputStream(embeddedFileDescriptionBox.getMediaTypeWithEscapeCharacter(),
                fileOutputStream);

        if (embeddedFileDescriptionBox.fileNameExists()) {
            CoreUtils
                    .writeTextToOutputStream(embeddedFileDescriptionBox.getFileNameWithEscapeCharacter(),
                            fileOutputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            long availableBytesForBox, InputStream input) throws MipamsException {
        logger.debug("Embedded File Description box");

        int toggleValue = CoreUtils.readSingleByteAsIntFromInputStream(input);
        embeddedFileDescriptionBox.setToggle(toggleValue);

        String mediaTypeAsString = CoreUtils.readStringFromInputStream(input);

        CoreUtils.addEscapeCharacterToText(mediaTypeAsString).length();

        try {
            embeddedFileDescriptionBox.setMediaTypeFromString(mediaTypeAsString);
        } catch (MipamsException e) {
            throw new BadRequestException(e);
        }

        String fileName = embeddedFileDescriptionBox.fileNameExists() ? CoreUtils.readStringFromInputStream(input)
                : embeddedFileDescriptionBox.getRandomFileName();

        CoreUtils.addEscapeCharacterToText(fileName).length();

        embeddedFileDescriptionBox.setFileName(fileName);

        logger.debug("Discovered box: " + embeddedFileDescriptionBox.toString());
    }
}
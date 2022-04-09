package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileDescriptionBoxService extends XtBoxService<EmbeddedFileDescriptionBox> {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileDescriptionBoxService.class);

    @Override
    protected EmbeddedFileDescriptionBox initializeBox() throws MipamsException {
        return new EmbeddedFileDescriptionBox();
    }

    @Override
    protected void populateBox(EmbeddedFileDescriptionBox embeddedFileDescriptionBox, ObjectNode input)
            throws MipamsException {

        int toggle = 0;

        try {
            embeddedFileDescriptionBox.setMediaTypeFromString(input.get("mediaType").asText());
        } catch (MipamsException e) {
            throw new BadRequestException(e);
        } catch (NullPointerException e) {
            throw new BadRequestException("Media type not specified", e);
        }

        JsonNode node = input.get("fileName");

        if (node != null) {
            toggle = 1;
            embeddedFileDescriptionBox.setFileName(node.asText());
        }

        node = input.get("fileExternallyReferenced");

        if (node != null) {
            toggle = toggle | 2;
            if (node.asBoolean()) {
                embeddedFileDescriptionBox.markFileAsExternallyReferenced();
            } else {
                embeddedFileDescriptionBox.markFileAsInternallyReferenced();
            }
        } else {
            embeddedFileDescriptionBox.markFileAsExternallyReferenced();
        }

        embeddedFileDescriptionBox.setToggle(toggle);
    }

    @Override
    protected void writeXtBoxPayloadToJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            FileOutputStream fileOutputStream) throws MipamsException {

        try {
            fileOutputStream
                    .write(CoreUtils.convertIntToSingleElementByteArray(embeddedFileDescriptionBox.getToggle()));
            fileOutputStream.write(
                    CoreUtils.convertStringToByteArray(embeddedFileDescriptionBox.getMediaTypeWithEscapeCharacter()));

            if (embeddedFileDescriptionBox.fileNameExists()) {
                fileOutputStream.write(CoreUtils
                        .convertStringToByteArray(embeddedFileDescriptionBox.getFileNameWithEscapeCharacter()));
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            InputStream input) throws MipamsException {
        logger.debug("Embedded File Description box");

        long actualSize = 0;

        try {

            int toggleValue = 0;
            if ((toggleValue = input.read()) == -1) {
                throw new MipamsException();
            }
            actualSize++;
            embeddedFileDescriptionBox.setToggle(toggleValue);

            String mediaTypeAsString = CoreUtils.readStringFromInputStream(input);

            // +1 for the null terminating character
            actualSize += CoreUtils.addEscapeCharacterToText(mediaTypeAsString).length();

            try {
                embeddedFileDescriptionBox.setMediaTypeFromString(mediaTypeAsString);
            } catch (MipamsException e) {
                throw new BadRequestException(e);
            }

            if (embeddedFileDescriptionBox.fileNameExists()) {
                String fileName = CoreUtils.readStringFromInputStream(input);

                // +1 for the null terminating character
                actualSize += CoreUtils.addEscapeCharacterToText(fileName).length();

                embeddedFileDescriptionBox.setFileName(fileName);
            }

        } catch (IOException e) {
            throw new MipamsException("Failed to read description box after {" + Long.toString(actualSize) + "} bytes.",
                    e);
        }

        verifyBoxSize(embeddedFileDescriptionBox, actualSize);

        logger.debug("Discovered box: " + embeddedFileDescriptionBox.toString());
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.EmbeddedFileDescriptionBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.EmbeddedFileDescriptionBox.getType();
    }
}
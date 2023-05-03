package org.mipams.jumbf.services.boxes;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileDescriptionBoxService extends BmffBoxService<EmbeddedFileDescriptionBox> {

    private static final Logger logger = Logger.getLogger(EmbeddedFileDescriptionBoxService.class.getName());

    ServiceMetadata serviceMetadata = new ServiceMetadata(EmbeddedFileDescriptionBox.TYPE_ID,
            EmbeddedFileDescriptionBox.TYPE);

    @Override
    protected EmbeddedFileDescriptionBox initializeBox() {
        return new EmbeddedFileDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            OutputStream outputStream) throws MipamsException {

        CoreUtils.writeIntAsSingleByteToOutputStream(embeddedFileDescriptionBox.getToggle(), outputStream);

        final String mediaTypeAsString = embeddedFileDescriptionBox.getMediaType().toString();

        CoreUtils.writeTextToOutputStream(CoreUtils.addEscapeCharacterToText(mediaTypeAsString), outputStream);

        if (embeddedFileDescriptionBox.fileNameExists()) {
            final String fileName = embeddedFileDescriptionBox.getFileName();
            CoreUtils.writeTextToOutputStream(CoreUtils.addEscapeCharacterToText(fileName), outputStream);
        }

    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileDescriptionBox embeddedFileDescriptionBox,
            ParseMetadata parseMetadata, InputStream input) throws MipamsException {
        logger.log(Level.FINE, "Embedded File Description box");

        int toggleValue = CoreUtils.readSingleByteAsIntFromInputStream(input);
        embeddedFileDescriptionBox.setToggle(toggleValue);

        String mediaTypeAsString = CoreUtils.readStringFromInputStream(input);

        CoreUtils.addEscapeCharacterToText(mediaTypeAsString).length();

        embeddedFileDescriptionBox.setMediaTypeFromString(mediaTypeAsString);

        String fileName = embeddedFileDescriptionBox.fileNameExists() ? CoreUtils.readStringFromInputStream(input)
                : null;
        embeddedFileDescriptionBox.setFileName(fileName);

        logger.log(Level.FINE, "Discovered box: " + embeddedFileDescriptionBox.toString());
    }
}
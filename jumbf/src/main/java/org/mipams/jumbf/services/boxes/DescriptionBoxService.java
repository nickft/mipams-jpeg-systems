package org.mipams.jumbf.services.boxes;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mipams.jumbf.BmffBoxServiceDiscoveryManager;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.DescriptionBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.PrivateBox;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

@Service
public final class DescriptionBoxService extends BmffBoxService<DescriptionBox> {

    private static final Logger logger = Logger.getLogger(DescriptionBoxService.class.getName());

    @Autowired
    BmffBoxServiceDiscoveryManager bmffBoxServiceDiscoveryManager;

    @Autowired
    PrivateBoxService privateBoxService;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        DescriptionBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected DescriptionBox initializeBox() {
        return new DescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(DescriptionBox descriptionBox, OutputStream outputStream)
            throws MipamsException {

        CoreUtils.writeUuidToOutputStream(descriptionBox.getUuid(), outputStream);
        CoreUtils.writeIntAsSingleByteToOutputStream(descriptionBox.getToggle(), outputStream);

        if (descriptionBox.labelExists()) {
            final String labelWithEscapeCharacter = CoreUtils.addEscapeCharacterToText(descriptionBox.getLabel());
            CoreUtils.writeTextToOutputStream(labelWithEscapeCharacter, outputStream);
        }

        if (descriptionBox.idExists()) {
            CoreUtils.writeIntToOutputStream(descriptionBox.getId(), outputStream);
        }

        if (descriptionBox.sha256HashExists()) {
            CoreUtils.writeByteArrayToOutputStream(descriptionBox.getSha256Hash(), outputStream);
        }

        if (descriptionBox.privateFieldExists()) {
            if (descriptionBox.getPrivateField().getClass().equals(PrivateBox.class)) {
                privateBoxService.writeToJumbfFile((PrivateBox) descriptionBox.getPrivateField(), outputStream);
            } else {
                BmffBoxService service = bmffBoxServiceDiscoveryManager
                        .getBmffBoxServiceBasedOnTbox(descriptionBox.getPrivateField().getTBox());
                service.writeToJumbfFile(descriptionBox.getPrivateField(), outputStream);
            }
        }
    }

    @Override
    protected void populatePayloadFromJumbfFile(DescriptionBox descriptionBox, ParseMetadata parseMetadata,
            InputStream input) throws MipamsException {

        logger.log(Level.FINE, "Parsing Description box");

        String uuid = CoreUtils.readUuidFromInputStream(input);
        descriptionBox.setUuid(uuid);
        long availableBytesForBox = parseMetadata.getAvailableBytesForBox() - CoreUtils.UUID_BYTE_SIZE;

        int toggleValue = CoreUtils.readSingleByteAsIntFromInputStream(input);
        descriptionBox.setToggle(toggleValue);
        availableBytesForBox--;

        if (descriptionBox.labelExists()) {
            String label = CoreUtils.readStringFromInputStream(input);
            descriptionBox.setLabel(label);
            availableBytesForBox -= CoreUtils.addEscapeCharacterToText(descriptionBox.getLabel()).length();
        }

        if (descriptionBox.idExists()) {
            int idVal = CoreUtils.readIntFromInputStream(input);
            descriptionBox.setId(idVal);
            availableBytesForBox -= CoreUtils.INT_BYTE_SIZE;
        }

        if (descriptionBox.sha256HashExists()) {
            byte[] sha256Hash = CoreUtils.readBytesFromInputStream(input, 32);
            descriptionBox.setSha256Hash(sha256Hash);
            availableBytesForBox -= 32;
        }

        ParseMetadata metadata = new ParseMetadata();
        metadata.setAvailableBytesForBox(availableBytesForBox);
        metadata.setParentDirectory(parseMetadata.getParentDirectory());

        BmffBox privateBox;
        if (descriptionBox.privateFieldExists()) {
            if (CoreUtils.isPrivateBoxNext(input)) {
                privateBox = privateBoxService.parseFromJumbfFile(input, metadata);
            } else {
                BmffBoxService service = getBmffBoxService(input);
                privateBox = service.parseFromJumbfFile(input, parseMetadata);
            }
            descriptionBox.setPrivateField(privateBox);
        }

        logger.log(Level.FINE, "Discovered box: " + descriptionBox.toString());
    }

    @SuppressWarnings("rawtypes")
    private BmffBoxService getBmffBoxService(InputStream input) throws MipamsException {
        input.mark(16);

        CoreUtils.readIntFromInputStream(input);
        int tBox = CoreUtils.readIntFromInputStream(input);

        try {
            if (!input.markSupported()) {
                throw new MipamsException("Input Stream does not support marking.");
            }

            input.reset();
            return bmffBoxServiceDiscoveryManager.getBmffBoxServiceBasedOnTbox(tBox);
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }
}
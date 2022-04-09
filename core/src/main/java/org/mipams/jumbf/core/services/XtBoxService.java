package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.XtBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XtBoxService<T extends XtBox> implements BoxServiceInterface<T> {

    private static final Logger logger = LoggerFactory.getLogger(XtBoxService.class);

    @Override
    public final T discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        T xtBox = initializeBox();

        try {
            validateRequestType(inputNode);
            populateBox(xtBox, inputNode);
        } catch (NullPointerException e) {
            throw new MipamsException("Error while parsing the request for box: " + serviceIsResponsibleForBoxType());
        }

        xtBox.updateXTHeadersBasedOnBox();

        return xtBox;
    }

    private void validateRequestType(ObjectNode inputNode) throws BadRequestException {
        JsonNode typeNode = inputNode.get("type");

        if (typeNode == null) {
            throw new BadRequestException("Box 'type' must be specified");
        }

        if (!serviceIsResponsibleForBoxType().equals(typeNode.asText())) {
            throw new BadRequestException("Box type does not match with description type.");
        }
    }

    protected abstract void populateBox(T box, ObjectNode input) throws MipamsException;

    @Override
    public void writeToJumbfFile(T box, FileOutputStream fileOutputStream) throws MipamsException {
        writeXtBoxHeadersToJumbfFile(box, fileOutputStream);
        writeXtBoxPayloadToJumbfFile(box, fileOutputStream);
    }

    private final void writeXtBoxHeadersToJumbfFile(T box, FileOutputStream fileOutputStream) throws MipamsException {
        try {

            fileOutputStream.write(CoreUtils.convertIntToByteArray(box.getLBox()));
            fileOutputStream.write(CoreUtils.convertIntToByteArray(box.getTBox()));

            if (box.isXBoxEnabled()) {
                fileOutputStream.write(CoreUtils.convertLongToByteArray(box.getXBox()));
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file", e);
        }
    }

    protected abstract void writeXtBoxPayloadToJumbfFile(T box, FileOutputStream fileOutputStream)
            throws MipamsException;

    @Override
    public final T parseFromJumbfFile(InputStream input) throws MipamsException {

        logger.debug("Start parsing a new XTBox");

        T xtBox = initializeBox();

        populateHeadersFromJumbfFile(xtBox, input);

        populatePayloadFromJumbfFile(xtBox, input);

        logger.debug("The box " + BoxTypeEnum.getBoxTypeAsStringFromId(xtBox.getTypeId()) + " has a total length of "
                + xtBox.getBoxSizeFromXTBoxHeaders());

        return xtBox;
    }

    protected abstract T initializeBox() throws MipamsException;

    private void populateHeadersFromJumbfFile(T box, InputStream input) throws MipamsException {
        byte[] temp = new byte[4];
        int value;

        try {

            if (input.available() == 0) {
                return;
            }

            if (input.read(temp, 0, 4) == -1) {
                throw new CorruptedJumbfFileException("Failed to parse LBox value from byte stream");
            }

            value = CoreUtils.convertByteArrayToInt(temp);
            box.setLBox(value);

            if (input.read(temp, 0, 4) == -1) {
                throw new CorruptedJumbfFileException("Failed to parse TBox value from byte stream");
            }

            value = CoreUtils.convertByteArrayToInt(temp);

            BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromIdOrNull(value);

            if (boxType != null && box.getTypeId() != boxType.getTypeId()) {
                throw new CorruptedJumbfFileException("TBox Id " + value + " does not match with box "
                        + BoxTypeEnum.getBoxTypeAsStringFromId(box.getTypeId()));
            }

            box.setTBox(value);

            if (box.getLBox() == 1) {
                temp = new byte[8];
                long longVal;

                if (input.read(temp, 0, 8) == -1) {
                    throw new CorruptedJumbfFileException("Failed to parse XBox value from byte stream");
                }

                longVal = CoreUtils.convertByteArrayToLong(temp);
                box.setXBox(longVal);
            }
        } catch (IOException e) {
            throw new MipamsException("Could not read XTBox values", e);
        }
    }

    protected void verifyBoxSize(T box, long actualSize) throws MipamsException {
        if (box.getPayloadSizeFromXTBoxHeaders() != actualSize) {
            throw new MipamsException("Mismatch in the byte counting(Nominal: " + box.getPayloadSizeFromXTBoxHeaders()
                    + ", Actual: " + Long.toString(actualSize) + ") of the Box: " + box.toString());
        }
    }

    protected abstract void populatePayloadFromJumbfFile(T box, InputStream input) throws MipamsException;

    public abstract String serviceIsResponsibleForBoxType();
}
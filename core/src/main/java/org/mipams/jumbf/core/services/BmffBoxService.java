package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BmffBoxService<T extends BmffBox> implements BoxServiceInterface<T> {

    private static final Logger logger = LoggerFactory.getLogger(BmffBoxService.class);

    @Override
    public final T discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        T bmffBox = initializeBox();

        try {
            validateRequestType(inputNode);
            populateBox(bmffBox, inputNode);
        } catch (NullPointerException e) {
            throw new MipamsException("Error while parsing the request for box: " + getServiceMetadata().toString(), e);
        }

        bmffBox.updateBmffHeadersBasedOnBox();

        return bmffBox;
    }

    private void validateRequestType(ObjectNode inputNode) throws BadRequestException {
        JsonNode typeNode = inputNode.get("type");

        if (typeNode == null) {
            throw new BadRequestException("Box 'type' must be specified");
        }

        String expectedType = getServiceMetadata().getBoxType();
        String requestedType = typeNode.asText();

        if (!expectedType.equals(requestedType)) {
            String errorMessage = generateErrorMessageForRequestValidation(expectedType, requestedType);
            throw new BadRequestException(errorMessage);
        }
    }

    private String generateErrorMessageForRequestValidation(String expectedType, String requestedType) {

        String errorMessage = String.format(
                "Box type does not match with description type. Expected: %s, Found: %s",
                expectedType, requestedType);

        return errorMessage;
    }

    protected abstract void populateBox(T box, ObjectNode input) throws MipamsException;

    @Override
    public void writeToJumbfFile(T bmffBox, FileOutputStream fileOutputStream) throws MipamsException {
        writeBmffHeadersToJumbfFile(bmffBox, fileOutputStream);
        writeBmffPayloadToJumbfFile(bmffBox, fileOutputStream);
    }

    private final void writeBmffHeadersToJumbfFile(T box, FileOutputStream fileOutputStream) throws MipamsException {
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

    protected abstract void writeBmffPayloadToJumbfFile(T box, FileOutputStream fileOutputStream)
            throws MipamsException;

    @Override
    public final T parseFromJumbfFile(InputStream input, long availableBytesForBox) throws MipamsException {

        logger.debug("Start parsing a new BMFF Box");

        T bmffBox = initializeBox();

        populateHeadersFromJumbfFile(bmffBox, input);

        populatePayloadFromJumbfFile(bmffBox, input);

        logger.debug("The box " + BoxTypeEnum.getBoxTypeAsStringFromId(bmffBox.getTypeId()) + " has a total length of "
                + bmffBox.getBoxSizeFromBmffHeaders());

        return bmffBox;
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
            throw new MipamsException("Could not read BMFF Box fields", e);
        }
    }

    protected void verifyBoxSize(T box, long actualSize) throws MipamsException {
        if (box.getPayloadSizeFromBmffHeaders() != actualSize) {
            throw new MipamsException("Mismatch in the byte counting(Nominal: " + box.getPayloadSizeFromBmffHeaders()
                    + ", Actual: " + Long.toString(actualSize) + ") of the Box: " + box.toString());
        }
    }

    protected abstract void populatePayloadFromJumbfFile(T box, InputStream input) throws MipamsException;
}
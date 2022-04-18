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

        populatePayloadFromJumbfFile(bmffBox, availableBytesForBox, input);

        verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(bmffBox);

        logger.debug("The box " + BoxTypeEnum.getBoxTypeAsStringFromId(bmffBox.getTypeId()) + " has a total length of "
                + bmffBox.getBoxSizeFromBmffHeaders());

        return bmffBox;
    }

    protected abstract T initializeBox() throws MipamsException;

    private void populateHeadersFromJumbfFile(T box, InputStream input) throws MipamsException {

        try {
            if (input.available() == 0) {
                return;
            }
        } catch (IOException e) {
            throw new MipamsException("Could not check for remaining input bytes");
        }

        int lBox = CoreUtils.readIntFromInputStream(input);
        box.setLBox(lBox);

        int tBox = CoreUtils.readIntFromInputStream(input);

        BoxTypeEnum boxType = BoxTypeEnum.getBoxTypeFromIdOrNull(tBox);

        if (boxType != null && box.getTypeId() != boxType.getTypeId()) {
            throw new CorruptedJumbfFileException("TBox Id " + tBox + " does not match with box "
                    + BoxTypeEnum.getBoxTypeAsStringFromId(box.getTypeId()));
        }

        box.setTBox(tBox);

        if (box.getLBox() == 1) {
            long xBox = CoreUtils.readLongFromInputStream(input);
            box.setXBox(xBox);
        }
    }

    protected void verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(T box) throws MipamsException {

        if (!actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(box)) {
            throw new MipamsException("Mismatch in the byte counting(Nominal: " + box.getBoxSizeFromBmffHeaders()
                    + ", Actual: " + Long.toString(box.calculateSizeFromBox()) + ") of the Box: " + box.toString());
        }
    }

    public boolean actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(T box) throws MipamsException {
        long actualBoxSize = box.calculateSizeFromBox();
        long boxSizeAsInBmffHeaders = box.getBoxSizeFromBmffHeaders();

        return (actualBoxSize == boxSizeAsInBmffHeaders);
    }

    protected abstract void populatePayloadFromJumbfFile(T box, long availableBytesForBox, InputStream input)
            throws MipamsException;
}
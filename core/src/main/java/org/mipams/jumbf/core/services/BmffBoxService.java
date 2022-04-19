package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BmffBoxService<T extends BmffBox> implements BoxServiceInterface<T> {

    private static final Logger logger = LoggerFactory.getLogger(BmffBoxService.class);

    @Override
    public void writeToJumbfFile(T bmffBox, FileOutputStream fileOutputStream) throws MipamsException {
        writeBmffHeadersToJumbfFile(bmffBox, fileOutputStream);
        writeBmffPayloadToJumbfFile(bmffBox, fileOutputStream);
    }

    private final void writeBmffHeadersToJumbfFile(T box, FileOutputStream fileOutputStream) throws MipamsException {

        CoreUtils.writeIntToOutputStream(box.getLBox(), fileOutputStream);
        CoreUtils.writeIntToOutputStream(box.getTBox(), fileOutputStream);

        if (box.isXBoxEnabled()) {
            CoreUtils.writeLongToOutputStream(box.getXBox(), fileOutputStream);
        }
    }

    protected abstract void writeBmffPayloadToJumbfFile(T box, FileOutputStream fileOutputStream)
            throws MipamsException;

    @Override
    public final T parseFromJumbfFile(InputStream input, long availableBytesForBox) throws MipamsException {

        logger.debug("Start parsing a new BMFF Box");

        T bmffBox = initializeBox();

        populateHeadersFromJumbfFile(bmffBox, input);

        availableBytesForBox = bmffBox.getPayloadSizeFromBmffHeaders();
        populatePayloadFromJumbfFile(bmffBox, availableBytesForBox, input);

        verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(bmffBox);

        logger.debug("The box " + Integer.toHexString(bmffBox.getTypeId()) + " has a total length of "
                + bmffBox.getBoxSizeFromBmffHeaders());

        return bmffBox;
    }

    protected abstract T initializeBox();

    private void populateHeadersFromJumbfFile(T box, InputStream input) throws MipamsException {

        int lBox = CoreUtils.readIntFromInputStream(input);
        box.setLBox(lBox);

        int tBox = CoreUtils.readIntFromInputStream(input);

        if (getServiceMetadata().getBoxTypeId() != tBox) {
            throw new CorruptedJumbfFileException("TBox Id " + Integer.toHexString(tBox) + " does not match with box "
                    + getServiceMetadata().getBoxTypeId());
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
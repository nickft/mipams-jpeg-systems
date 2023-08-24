package org.mipams.jumbf.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.CorruptedJumbfFileException;

public abstract class BmffBoxService<T extends BmffBox> implements BoxServiceInterface<T> {

    private static Logger logger = Logger.getLogger(BmffBoxService.class.getName());

    @Override
    public void writeToJumbfFile(T bmffBox, OutputStream outputStream) throws MipamsException {
        writeBmffHeadersToJumbfFile(bmffBox, outputStream);
        writeBmffPayloadToJumbfFile(bmffBox, outputStream);
    }

    private final void writeBmffHeadersToJumbfFile(T box, OutputStream outputStream) throws MipamsException {

        CoreUtils.writeIntToOutputStream(box.getLBox(), outputStream);
        CoreUtils.writeIntToOutputStream(box.getTBox(), outputStream);

        if (box.isXBoxEnabled()) {
            CoreUtils.writeLongToOutputStream(box.getXlBox(), outputStream);
        }
    }

    protected abstract void writeBmffPayloadToJumbfFile(T box, OutputStream outputStream)
            throws MipamsException;

    @Override
    public final T parseFromJumbfFile(InputStream input, ParseMetadata parseMetadata) throws MipamsException {

        logger.log(Level.FINE, "Start parsing a new BMFF Box");

        T bmffBox = initializeBox();

        populateHeadersFromJumbfFile(bmffBox, input);

        ParseMetadata bmffPayloadParseMetadata = parseMetadata.clone();
        bmffPayloadParseMetadata.setAvailableBytesForBox(bmffBox.getPayloadSizeFromBmffHeaders());

        populatePayloadFromJumbfFile(bmffBox, bmffPayloadParseMetadata, input);

        verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(bmffBox);

        logger.log(Level.FINE, "The box " + Integer.toHexString(bmffBox.getTypeId()) + " has a total length of "
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
            long xlBox = CoreUtils.readLongFromInputStream(input);
            box.setXlBox(xlBox);
        }
    }

    public void verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(T box) throws MipamsException {

        if (!actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(box)) {
            throw new MipamsException("Mismatch in the byte counting(Nominal: " + box.getBoxSizeFromBmffHeaders()
                    + ", Actual: " + Long.toString(box.calculateSizeFromBox()) + ") of the Box: " + box.toString());
        }
    }

    public boolean actualBoxSizeEqualsToSizeSpecifiedInBmffHeaders(T box) throws MipamsException {

        long boxSizeAsInBmffHeaders = box.getBoxSizeFromBmffHeaders();

        if (boxSizeAsInBmffHeaders == 0) {
            return true;
        }

        long actualBoxSize = box.calculateSizeFromBox();

        return (actualBoxSize == boxSizeAsInBmffHeaders);
    }

    protected abstract void populatePayloadFromJumbfFile(T box, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException;
}
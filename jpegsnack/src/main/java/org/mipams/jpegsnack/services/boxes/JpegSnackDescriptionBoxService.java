package org.mipams.jpegsnack.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jpegsnack.entities.Composition;
import org.mipams.jpegsnack.entities.JpegSnackDescriptionBox;
import org.mipams.jpegsnack.util.JpegSnackException;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.boxes.BmffBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class JpegSnackDescriptionBoxService extends BmffBoxService<JpegSnackDescriptionBox> {
    ServiceMetadata serviceMetadata = new ServiceMetadata(JpegSnackDescriptionBox.TYPE_ID,
            JpegSnackDescriptionBox.TYPE);

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected JpegSnackDescriptionBox initializeBox() {
        return new JpegSnackDescriptionBox();
    }

    @Override
    protected void populatePayloadFromJumbfFile(JpegSnackDescriptionBox jpegSnackDescriptionBox, ParseMetadata metadata,
            InputStream is) throws MipamsException {

        long availableBytesForBox = metadata.getAvailableBytesForBox();

        int vesion = CoreUtils.readSingleByteAsIntFromInputStream(is);
        jpegSnackDescriptionBox.setVersion(vesion);
        availableBytesForBox--;

        long startTime = CoreUtils.readLongFromInputStream(is);
        jpegSnackDescriptionBox.setStartTime(startTime);
        availableBytesForBox -= CoreUtils.LONG_BYTE_SIZE;

        int value = CoreUtils.readSingleByteAsIntFromInputStream(is);
        availableBytesForBox--;

        getCompositions(jpegSnackDescriptionBox, value, availableBytesForBox, is);
    }

    private void getCompositions(JpegSnackDescriptionBox jsdb, int value, long availableBytesForBox, InputStream is)
            throws MipamsException {

        boolean noOfCompositionsExists = false;

        if (value != availableBytesForBox) {
            if (value != 1) {
                throw new JpegSnackException("Only one composition is currently supported");
            }
            jsdb.setNoOfCompositions(value);
            noOfCompositionsExists = true;
        }

        int iterations = noOfCompositionsExists ? jsdb.getNoOfCompositions() : 1;

        for (int i = 0; i < iterations; i++) {
            Composition composition = new Composition();

            if (noOfCompositionsExists) {
                int compositionId = CoreUtils.readSingleByteAsIntFromInputStream(is);
                composition.setId(compositionId);
            }

            int noOfObjects = noOfCompositionsExists ? CoreUtils.readSingleByteAsIntFromInputStream(is) : value;
            composition.setNoOfObjects(noOfObjects);

            for (int j = 0; j < noOfObjects; j++) {
                int objectId = CoreUtils.readSingleByteAsIntFromInputStream(is);
                composition.getObjectIds().add(objectId);
            }
            jsdb.getCompositions().add(composition);
        }

    }

    @Override
    protected void writeBmffPayloadToJumbfFile(JpegSnackDescriptionBox jsdb, OutputStream os) throws MipamsException {

        if (jsdb.getVersion() != 1) {
            throw new JpegSnackException(
                    String.format("Unsupported version. JPEG Snack version 1 is supported. Found: %d",
                            jsdb.getVersion()));
        }

        CoreUtils.writeIntAsSingleByteToOutputStream(jsdb.getVersion(), os);
        CoreUtils.writeLongToOutputStream(jsdb.getStartTime(), os);

        boolean noOfCompositionExists = jsdb.getNoOfCompositions() != null;
        if (noOfCompositionExists) {
            CoreUtils.writeIntAsSingleByteToOutputStream(jsdb.getNoOfCompositions(), os);
        }

        if (jsdb.getCompositions().size() != 1) {
            throw new JpegSnackException(String.format("Only one composition is supported in version 1. Found %d",
                    jsdb.getCompositions().size()));
        }

        for (Composition composition : jsdb.getCompositions()) {
            if (noOfCompositionExists) {
                CoreUtils.writeIntAsSingleByteToOutputStream(composition.getId(), os);
            }
            CoreUtils.writeIntAsSingleByteToOutputStream(composition.getNoOfObjects(), os);
            if (composition.getNoOfObjects() != composition.getObjectIds().size()) {
                throw new JpegSnackException(
                        String.format("Inconsistency between declared size [%d] and actual size [%d]",
                                composition.getNoOfObjects(), composition.getObjectIds().size()));
            }

            for (Integer objectId : composition.getObjectIds()) {
                CoreUtils.writeIntAsSingleByteToOutputStream(objectId, os);
            }
        }
    }
}

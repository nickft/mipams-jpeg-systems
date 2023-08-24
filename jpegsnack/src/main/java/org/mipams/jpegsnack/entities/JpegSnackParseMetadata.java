package org.mipams.jpegsnack.entities;

import org.mipams.jumbf.entities.ParseMetadata;

public class JpegSnackParseMetadata extends ParseMetadata {

    int noOfObjects;

    public void setNoOfObjects(int noOfObjects) {
        this.noOfObjects = noOfObjects;
    }

    public int getNoOfObjects() {
        return this.noOfObjects;
    }

    @Override
    public ParseMetadata clone() {
        JpegSnackParseMetadata metadata = new JpegSnackParseMetadata();

        metadata.setAvailableBytesForBox(getAvailableBytesForBox());
        metadata.setParentDirectory(getParentDirectory());
        metadata.setNoOfObjects(noOfObjects);

        return metadata;
    }

}

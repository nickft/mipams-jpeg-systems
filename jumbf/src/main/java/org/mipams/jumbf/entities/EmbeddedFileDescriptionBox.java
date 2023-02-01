package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.http.MediaType;

public class EmbeddedFileDescriptionBox extends BmffBox {

    private int toggle;

    private MediaType mediaType;

    private String fileName;

    @Override
    public int getTypeId() {
        return 0x62666462;
    }

    @Override
    public String getType() {
        return "bfdb";
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum = getToggleSize();

        sum += getMediaTypeSize();

        if (fileNameExists())
            sum += getFileNameSize();

        return sum;
    }

    public int getToggleSize() {
        return 1;
    }

    public int getMediaTypeSize() {
        return CoreUtils.addEscapeCharacterToText(getMediaType().toString()).length();
    }

    public int getFileNameSize() {
        return CoreUtils.addEscapeCharacterToText(getFileName()).length();
    }

    public boolean fileNameExists() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 0);
    }

    public boolean isContentReferencedExternally() {
        return CoreUtils.isBitAtGivenPositionSet(toggle, 1);
    }

    public void markFileAsExternallyReferenced() {
        int value = 1;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 1, value);
        setToggle(updatedToggle);
    }

    public void markFileAsInternallyReferenced() {
        int value = 0;
        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 1, value);
        setToggle(updatedToggle);
    }

    public void setMediaTypeFromString(String type) throws MipamsException {
        try {
            MediaType mediaType = CoreUtils.getMediaTypeFromString(type);
            setMediaType(mediaType);
        } catch (IllegalArgumentException e) {
            throw new MipamsException("Bad Media Type", e);
        }
    }

    public String discoverFileName() {
        if (fileNameExists()) {
            return getFileName();
        } else {
            return getRandomFileName();

        }
    }

    public String getRandomFileName() {
        return CoreUtils.randomStringGenerator() + "." + getMediaType().getSubtype();
    }

    public int getToggle() {
        return this.toggle;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;
    }

    public MediaType getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {

        int value;

        if (fileName == null) {
            value = 0;
        } else {
            value = 1;
        }

        int updatedToggle = CoreUtils.setBitValueAtGivenPosition(getToggle(), 0, value);
        setToggle(updatedToggle);
        this.fileName = fileName;
    }

    @Override
    public String toString() {

        final String mediaType = this.mediaType != null ? getMediaType().toString() : "null";

        final String fileName = this.fileName != null ? getFileName() : "null";

        return "EmbeddedFileDescriptionBox(" + super.toString() + ", toggle=" + getToggle() + ", mediaType=" + mediaType
                + ", fileName=" + fileName + ")";
    }

}
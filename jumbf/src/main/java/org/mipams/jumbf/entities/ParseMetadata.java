package org.mipams.jumbf.entities;

public class ParseMetadata {
    private long availableBytesForBox;
    private String parentDirectory;

    public long getAvailableBytesForBox() {
        return this.availableBytesForBox;
    }

    public void setAvailableBytesForBox(long availableBytesForBox) {
        this.availableBytesForBox = availableBytesForBox;
    }

    public String getParentDirectory() {
        return this.parentDirectory;
    }

    public void setParentDirectory(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }
}

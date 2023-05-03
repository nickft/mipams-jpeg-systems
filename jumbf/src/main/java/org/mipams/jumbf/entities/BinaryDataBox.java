package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class BinaryDataBox extends FileBox {

    private boolean referencedExternally = false;

    public static int TYPE_ID = 0x62696462;
    public static String TYPE = "bidb";

    @Override
    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected long calculatePayloadSize() throws MipamsException {

        long sum;

        if (isReferencedExternally()) {
            sum = getUrlSize();
        } else {
            sum = getFileSize();
        }

        return sum;
    }

    private long getFileSize() throws MipamsException {
        return CoreUtils.getFileSizeFromPath(getFileUrl());
    }

    private long getUrlSize() {
        return CoreUtils.addEscapeCharacterToText(getFileUrl()).length();
    }

    public boolean isReferencedExternally() {
        return this.referencedExternally;
    }

    public void setReferencedExternally(boolean referencedExternally) {
        this.referencedExternally = referencedExternally;
    }

    @Override
    public String toString() {
        return "BinaryDataBox(" + super.toString() + ", referencedExternally=" + referencedExternally + ")";
    }
}

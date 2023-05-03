package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.MipamsException;

public class PaddingBox extends BmffBox {

    public static int PADDING_VALUE = 0x00;

    private long paddingSize;

    public static int TYPE_ID = 0x66726565;
    public static String TYPE = "free";

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
        return getPaddingSize();
    }

    public long getPaddingSize() {
        return this.paddingSize;
    }

    public void setPaddingSize(long paddingSize) {
        this.paddingSize = paddingSize;
    }

    @Override
    public String toString() {
        return "PaddingBox(" + super.toString() + ", paddingSize=" + getPaddingSize() + ")";
    }

}

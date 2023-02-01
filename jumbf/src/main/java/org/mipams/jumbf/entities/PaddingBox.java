package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.MipamsException;

public class PaddingBox extends BmffBox {

    public static int PADDING_VALUE = 0x00;

    private long paddingSize;

    @Override
    public int getTypeId() {
        return 0x66726565;
    }

    @Override
    public String getType() {
        return "free";
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

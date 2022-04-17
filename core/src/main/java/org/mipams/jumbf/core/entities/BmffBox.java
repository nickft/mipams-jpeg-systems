package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class BmffBox implements BoxInterface {

    private @Getter @Setter int LBox;

    private @Getter @Setter int TBox;

    private @Getter @Setter Long XBox;

    @Override
    public final long getBoxSize() {
        return getBoxSizeFromBmffHeaders();
    }

    public final long getPayloadSizeFromBmffHeaders() {

        long payloadSize = getBoxSizeFromBmffHeaders();

        payloadSize -= getLBoxSize() + getTBoxSize();

        if (isXBoxEnabled())
            payloadSize -= getXBoxSize();

        return payloadSize;
    }

    public final long getBoxSizeFromBmffHeaders() {
        return isXBoxEnabled() ? getXBox() : getLBox();
    }

    public final boolean isXBoxEnabled() {
        return LBox == 1 && (XBox != null);
    }

    public final void updateBmffHeadersBasedOnBox() throws MipamsException {

        setTBox(getTypeId());

        long size = calculateSizeFromBox();

        if (size > Integer.MAX_VALUE) {
            size += getXBoxSize();
            setLBox(1);
            setXBox(size);
        } else {
            setLBox((int) size);
        }
    }

    private long calculateSizeFromBox() throws MipamsException {
        return getLBoxSize() + getTBoxSize() + calculatePayloadSize();
    }

    private int getLBoxSize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    private int getTBoxSize() {
        return CoreUtils.INT_BYTE_SIZE;
    }

    private int getXBoxSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

    protected abstract long calculatePayloadSize() throws MipamsException;

}

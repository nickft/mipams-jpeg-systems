package org.mipams.jumbf.core.entities;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public abstract class BmffBox implements BoxInterface {

    private @Getter @Setter int LBox;

    private @Getter @Setter int TBox;

    private @Getter @Setter Long XlBox;

    @Override
    public final long getBoxSize() {
        return getBoxSizeFromBmffHeaders();
    }

    public final long getPayloadSizeFromBmffHeaders() {

        long payloadSize = getBoxSizeFromBmffHeaders();

        payloadSize -= getLBoxSize() + getTBoxSize();

        if (isXBoxEnabled()) {
            payloadSize -= getXBoxSize();
        }

        return payloadSize;
    }

    public final long getBoxSizeFromBmffHeaders() {
        return isXBoxEnabled() ? getXlBox() : getLBox();
    }

    public final boolean isXBoxEnabled() {
        return LBox == 1 && (getXlBox() != null);
    }

    public final void updateBmffHeadersBasedOnBox() throws MipamsException {

        setTBox(getTypeId());

        long size = calculateSizeFromBox();

        if (isXBoxRequiredBasedOnSize(size)) {
            setLBox(1);
            setXlBox(size);
        } else {
            setLBox((int) size);
        }
    }

    public long calculateSizeFromBox() throws MipamsException {
        long sum = Long.valueOf(getLBoxSize() + getTBoxSize()) + calculatePayloadSize();

        if (isXBoxRequiredBasedOnSize(sum)) {
            sum += getXBoxSize();
        }

        return sum;
    }

    private boolean isXBoxRequiredBasedOnSize(long size) {
        return CoreUtils.numberOfHexCharsToRepresentLong(size) > getLBoxSize() * 2;
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

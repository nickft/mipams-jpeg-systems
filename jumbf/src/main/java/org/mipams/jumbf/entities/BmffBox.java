package org.mipams.jumbf.entities;

import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public abstract class BmffBox implements BoxInterface {

    private int lBox;

    private int tBox;

    private Long xlBox;

    @Override
    public final long getBoxSize() {
        return getBoxSizeFromBmffHeaders();
    }

    public final long getPayloadSizeFromBmffHeaders() {

        if (shouldReadUntilEOF()) {
            return 0;
        }

        long payloadSize = getBoxSizeFromBmffHeaders();

        payloadSize -= getLBoxSize() + getTBoxSize();

        if (isXBoxEnabled()) {
            payloadSize -= getXBoxSize();
        }

        return payloadSize;
    }

    private boolean shouldReadUntilEOF() {
        return getLBox() == 0;
    }

    public final long getBoxSizeFromBmffHeaders() {
        return isXBoxEnabled() ? getXlBox() : getLBox();
    }

    public final boolean isXBoxEnabled() {
        return getLBox() == 1 && (getXlBox() != null);
    }

    public final void updateFieldsBasedOnExistingData() throws MipamsException {
        applyInternalBoxFieldsBasedOnExistingData();
        updateBmffHeadersBasedOnBox();
    }

    protected void applyInternalBoxFieldsBasedOnExistingData() throws MipamsException {
    }

    private void updateBmffHeadersBasedOnBox() throws MipamsException {
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

    public int getLBox() {
        return this.lBox;
    }

    public void setLBox(int lBox) {
        this.lBox = lBox;
    }

    public int getTBox() {
        return this.tBox;
    }

    public void setTBox(int tBox) {
        this.tBox = tBox;
    }

    public Long getXlBox() {
        return this.xlBox;
    }

    public void setXlBox(Long xlBox) {
        this.xlBox = xlBox;
    }

    @Override
    public String toString() {
        final String xlBox = this.xlBox != null ? getXlBox().toString() : "null";
        return "BmffBox(lBox=" + getLBox() + ", tBox=" + tBox + ", xlBox=" + xlBox + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        BmffBox that = (BmffBox) o;

        boolean xlBoxEquality = getXlBox() == null ? that.getXlBox() == null : getXlBox().equals(that.getXlBox());

        return getLBox() == that.getLBox() &&
                getTBox() == that.getTBox() &&
                xlBoxEquality;
    }
}

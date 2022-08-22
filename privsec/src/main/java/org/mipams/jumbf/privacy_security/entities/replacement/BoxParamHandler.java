package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

public class BoxParamHandler implements ParamHandlerInterface {

    private long offset;
    private String label;

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {
        CoreUtils.writeLongToOutputStream(getOffset(), outputStream);

        if (labelExists()) {
            CoreUtils.writeTextToOutputStream(getLabelWithEscapeCharacter(), outputStream);
        }
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {

        long offset = CoreUtils.readLongFromInputStream(inputStream);
        setOffset(offset);

        if (labelExists()) {
            String label = CoreUtils.readStringFromInputStream(inputStream);
            setLabel(label);
        }
    }

    @Override
    public long getParamSize() throws MipamsException {
        long sum = 0;

        sum += getOffsetSize();

        if (labelExists()) {
            sum += getLabelSize();
        }

        return sum;
    }

    private long getOffsetSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

    private long getLabelSize() {
        return getLabelWithEscapeCharacter().length();
    }

    public boolean labelExists() {
        return offsetHasMaxValue();
    }

    private boolean offsetHasMaxValue() {
        return Long.valueOf(getMaxLongValue()).equals(getOffset());
    }

    public long getMaxLongValue() {
        String maxLongAsHex = "FFFFFFFFFFFFFFFF";
        BigInteger maxValue = new BigInteger(maxLongAsHex, 16);
        return maxValue.longValue();
    }

    public String getLabelWithEscapeCharacter() {
        return CoreUtils.addEscapeCharacterToText(getLabel());
    }

    public Long getOffset() {
        return this.offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {

        final String offset = getOffset().toString();
        final String label = (this.label != null ? getLabel().toString() : "null");

        return "BoxParamHandler(offset=" + offset + ", label=" + label + ")";
    }
}

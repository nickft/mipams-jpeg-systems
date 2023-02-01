package org.mipams.privsec.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public class AppParamHandler implements ParamHandlerInterface {

    private Long offset = 0L;

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {
        if (offsetExists()) {
            CoreUtils.writeLongToOutputStream(getOffset(), outputStream);
        }
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream, long remainingBytes) throws MipamsException {
        if (remainingBytes == 0) {
            return;
        }

        long offset = CoreUtils.readLongFromInputStream(inputStream);
        setOffset(offset);
    }

    @Override
    public long getParamSize() throws MipamsException {
        return getOffset() != null ? getOffsetSize() : 0;
    }

    public boolean offsetExists() {
        return getOffset() != null;
    }

    private long getOffsetSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

    public Long getOffset() {
        return this.offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "AppParamHandler(offset=" + getOffset() != null ? getOffset().toString() : "null" + ")";
    }

}

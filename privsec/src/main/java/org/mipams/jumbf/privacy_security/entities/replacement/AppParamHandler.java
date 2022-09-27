package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

public class AppParamHandler implements ParamHandlerInterface {

    private Long offset;

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {

        CoreUtils.writeLongToOutputStream(getOffset(), outputStream);
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {
        long offset = CoreUtils.readLongFromInputStream(inputStream);
        setOffset(offset);
    }

    @Override
    public long getParamSize() throws MipamsException {
        return getOffsetSize();
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
        return "AppParamHandler(offset=" + this.offset != null ? getOffset().toString() : "null" + ")";
    }

}

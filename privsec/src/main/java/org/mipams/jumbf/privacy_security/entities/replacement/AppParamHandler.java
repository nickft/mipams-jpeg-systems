package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class AppParamHandler implements ParamHandlerInterface {

    private @Getter @Setter Long offset;

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

}

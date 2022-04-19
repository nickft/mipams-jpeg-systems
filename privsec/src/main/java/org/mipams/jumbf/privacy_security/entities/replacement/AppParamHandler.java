package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.IOException;
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
        try {
            if (offsetExists()) {
                outputStream.write(CoreUtils.convertLongToByteArray(getOffset()));
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {
        long offset = CoreUtils.readLongFromInputStream(inputStream);
        setOffset(offset);
    }

    @Override
    public long getParamSize() throws MipamsException {
        long sum = 0;

        if (offsetExists()) {
            sum += getOffsetSize();
        }

        return sum;
    }

    public boolean offsetExists() {
        return getOffset() != null;
    }

    private long getOffsetSize() {
        return CoreUtils.LONG_BYTE_SIZE;
    }

}

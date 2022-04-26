package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.util.MipamsException;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class EmptyParamHandler implements ParamHandlerInterface {

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream) throws MipamsException {
    }

    @Override
    public long getParamSize() throws MipamsException {
        return 0;
    }
}

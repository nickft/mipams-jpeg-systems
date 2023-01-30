package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.util.MipamsException;

public class FileParamHandler implements ParamHandlerInterface {

    @Override
    public void writeParamToBytes(OutputStream outputStream) throws MipamsException {
    }

    @Override
    public void populateParamFromBytes(InputStream inputStream, long remainingBytes) throws MipamsException {
    }

    @Override
    public long getParamSize() throws MipamsException {
        return 0;
    }

    @Override
    public String toString() {
        return "EmptyParamHandler()";
    }
}

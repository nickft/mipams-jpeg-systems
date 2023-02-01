package org.mipams.privsec.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.util.MipamsException;

public interface ParamHandlerInterface {

    public void writeParamToBytes(OutputStream outputStream) throws MipamsException;

    public void populateParamFromBytes(InputStream inputStream, long remainingBytes) throws MipamsException;

    public long getParamSize() throws MipamsException;
}

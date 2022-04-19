package org.mipams.jumbf.privacy_security.entities.replacement;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.util.MipamsException;

public interface ParamHandlerInterface {

    public void writeParamToBytes(OutputStream outputStream) throws MipamsException;

    public void populateParamFromBytes(InputStream inputStream) throws MipamsException;

    public long getParamSize() throws MipamsException;
}

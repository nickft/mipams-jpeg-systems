package org.mipams.jumbf.core.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.entities.BoxInterface;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.MipamsException;

public interface BoxServiceInterface<T extends BoxInterface> {

    public ServiceMetadata getServiceMetadata();

    public void writeToJumbfFile(T inputBox, OutputStream output) throws MipamsException;

    public T parseFromJumbfFile(InputStream input, ParseMetadata parseMetadata) throws MipamsException;

}
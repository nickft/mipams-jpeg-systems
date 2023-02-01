package org.mipams.jumbf.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.entities.BoxInterface;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.MipamsException;

public interface BoxServiceInterface<T extends BoxInterface> {

    public ServiceMetadata getServiceMetadata();

    public void writeToJumbfFile(T inputBox, OutputStream output) throws MipamsException;

    public T parseFromJumbfFile(InputStream input, ParseMetadata parseMetadata) throws MipamsException;

}
package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BoxInterface;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.MipamsException;

public interface BoxServiceInterface<T extends BoxInterface> {

    public ServiceMetadata getServiceMetadata();

    public T discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException;

    public void writeToJumbfFile(T inputBox, FileOutputStream output) throws MipamsException;

    public T parseFromJumbfFile(InputStream input, long availableBytesForBox) throws MipamsException;

}
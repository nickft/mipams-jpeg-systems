package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.mipams.jumbf.core.entities.BoxInterface;
import org.mipams.jumbf.core.util.MipamsException;

public interface BoxServiceInterface<T extends BoxInterface> {

    public void writeToJumbfFile(T inputBox, FileOutputStream output) throws MipamsException;

    public T parseFromJumbfFile(InputStream input) throws MipamsException;

}
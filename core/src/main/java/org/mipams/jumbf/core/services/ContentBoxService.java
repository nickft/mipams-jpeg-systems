package org.mipams.jumbf.core.services;

import java.io.InputStream;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface ContentBoxService<T extends ContentBox> extends BoxServiceInterface<T> {

    @Override
    public T parseFromJumbfFile(InputStream input, long availableBytesForBox) throws MipamsException;

}

package org.mipams.jumbf.core.services;

import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface ContentBoxService<T extends ContentBox> extends BoxServiceInterface<T> {

    @Override
    public T discoverBoxFromRequest(ObjectNode input) throws MipamsException;

    @Override
    public T parseFromJumbfFile(InputStream input) throws MipamsException;
}

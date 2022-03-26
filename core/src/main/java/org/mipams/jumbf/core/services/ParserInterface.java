package org.mipams.jumbf.core.services;

import java.io.InputStream;

import org.mipams.jumbf.core.util.MipamsException;

public interface ParserInterface{

    public String parseMetadataFromJumbfFile(String path) throws MipamsException;

}
package org.mipams.jumbf.core.services;

import java.util.List;

import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface ParserInterface {

    public List<XTBox> parseMetadataFromJumbfFile(String path) throws MipamsException;

}
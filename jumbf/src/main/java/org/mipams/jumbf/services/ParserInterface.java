package org.mipams.jumbf.services;

import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.util.MipamsException;

public interface ParserInterface {

    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws MipamsException;

}
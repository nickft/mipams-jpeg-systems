package org.mipams.jumbf.core.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.entities.XTBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface {

    public List<XTBox> generateBoxFromRequest(JsonNode input) throws MipamsException;

    public String generateJumbfFileFromBox(List<XTBox> xtBoxList) throws MipamsException;

}
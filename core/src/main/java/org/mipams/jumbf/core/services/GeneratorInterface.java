package org.mipams.jumbf.core.services;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface {

    public List<JumbfBox> generateBoxFromRequest(JsonNode input) throws MipamsException;

    public String generateJumbfFileFromBox(List<JumbfBox> jumbfBoxList, String fileName) throws MipamsException;

}
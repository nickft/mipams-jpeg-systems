package mipams.jumbf.core.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import mipams.jumbf.core.entities.XTBox;
import mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface{

    public String generateJumbfFileFromRequest(ObjectNode input) throws MipamsException;

}
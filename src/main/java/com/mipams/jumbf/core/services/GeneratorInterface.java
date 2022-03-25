package com.mipams.jumbf.core.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.entities.XTBox;
import com.mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface{

    public String generateJumbfFileFromRequest(ObjectNode input) throws MipamsException;

}
package com.mipams.jumbf.core.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface{

    public void generateJumbfFileFromRequest(ObjectNode input) throws MipamsException;

}
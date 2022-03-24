package com.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.mipams.jumbf.core.entities.BoxInterface;
import com.mipams.jumbf.core.util.MipamsException;

public interface BoxServiceInterface{

    public BoxInterface writeToJumbfFileFromRequest(ObjectNode input, FileOutputStream output) throws MipamsException;

    public BoxInterface parseFromJumbfFile(InputStream input) throws MipamsException;

}
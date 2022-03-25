package mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mipams.jumbf.core.entities.BoxInterface;
import mipams.jumbf.core.util.MipamsException;

public interface BoxServiceInterface<T>{

    public T writeToJumbfFileFromRequest(ObjectNode input, FileOutputStream output) throws MipamsException;

    public T parseFromJumbfFile(InputStream input) throws MipamsException;

}
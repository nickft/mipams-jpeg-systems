package com.mipams.jumbf.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;

import java.io.InputStream;
import java.io.FileOutputStream;

public interface BoxInterface {
    public void toBytes(FileOutputStream fileOutputStream) throws MipamsException;

    public void parse(InputStream input) throws MipamsException;
}
package com.mipams.jumbf.core;

import java.io.InputStream;

import com.mipams.jumbf.core.util.MipamsException;

public interface ParserInterface{

    public void parse(String path) throws MipamsException;

}
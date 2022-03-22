package com.mipams.jumbf.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

public interface BoxInterface {
    public void toBytes(FileOutputStream fileOutputStream) throws Exception;

    public void parse(ByteArrayInputStream input) throws Exception;
}
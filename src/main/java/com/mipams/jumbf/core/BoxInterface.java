package com.mipams.jumbf.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public interface BoxInterface {
    public ByteArrayOutputStream toBytes() throws Exception;

    public void parse(ByteArrayInputStream input) throws Exception;
}
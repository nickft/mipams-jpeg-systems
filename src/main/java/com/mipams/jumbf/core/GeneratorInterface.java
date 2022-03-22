package com.mipams.jumbf.core;

import java.io.ByteArrayInputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface GeneratorInterface{

    public void generate(ObjectNode input) throws Exception;

}
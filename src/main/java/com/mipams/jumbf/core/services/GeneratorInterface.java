package com.mipams.jumbf.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mipams.jumbf.core.util.MipamsException;

public interface GeneratorInterface{

    public void generate(ObjectNode input) throws MipamsException;

}
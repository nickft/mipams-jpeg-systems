package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BoxInterface;
import org.mipams.jumbf.core.util.MipamsException;

public interface BoxParserInterface {

    public BoxInterface discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException;
}

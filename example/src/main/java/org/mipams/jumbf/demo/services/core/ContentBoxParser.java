package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ContentBox;
import org.mipams.jumbf.core.util.MipamsException;

public interface ContentBoxParser extends BoxParserInterface {
    @Override
    public ContentBox discoverBoxFromRequest(ObjectNode input) throws MipamsException;

}

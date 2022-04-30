package org.mipams.jumbf.demo.entities.privacy_security.replacement;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.EmptyParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;

public class EmptyParamParser implements ParamParserInterface {

    public ParamHandlerInterface populateParamFromRequest(ObjectNode input) throws MipamsException {
        return new EmptyParamHandler();
    }
}

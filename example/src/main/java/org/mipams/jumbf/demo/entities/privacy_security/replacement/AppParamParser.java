package org.mipams.jumbf.demo.entities.privacy_security.replacement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.AppParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;

public class AppParamParser implements ParamParserInterface {

    @Override
    public ParamHandlerInterface populateParamFromRequest(ObjectNode input) throws MipamsException {

        AppParamHandler paramHandler = new AppParamHandler();

        JsonNode offsetNode = input.get("offset");

        if (offsetNode != null) {
            paramHandler.setOffset(offsetNode.asLong());
        }

        return paramHandler;
    }
}

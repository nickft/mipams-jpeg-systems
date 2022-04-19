package org.mipams.jumbf.demo.entities.privacy_security.replacement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.BoxParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;

public class BoxParamParser implements ParamParserInterface {

    public ParamHandlerInterface populateParamFromRequest(ObjectNode input) throws MipamsException {

        BoxParamHandler paramHandler = new BoxParamHandler();

        JsonNode offsetNode = input.get("offset");

        if (offsetNode != null) {
            paramHandler.setOffset(offsetNode.asLong());
        }

        JsonNode labelNode = input.get("label");

        if (labelNode != null) {
            paramHandler.setOffset(paramHandler.getMaxLongValue());
            paramHandler.setLabel(labelNode.asText());
        }

        return paramHandler;
    }
}

package org.mipams.jumbf.demo.entities.privacy_security.replacement;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.RoiParamHandler;

public class RoiParamParser implements ParamParserInterface {

    public ParamHandlerInterface populateParamFromRequest(ObjectNode input) throws MipamsException {

        RoiParamHandler paramHandler = new RoiParamHandler();

        int offsetX = input.get("offset-X").asInt();
        int offsetY = input.get("offset-Y").asInt();

        paramHandler.setOffsetX(offsetX);
        paramHandler.setOffsetY(offsetY);

        return paramHandler;
    }
}

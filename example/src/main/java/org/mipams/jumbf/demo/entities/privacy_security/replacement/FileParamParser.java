package org.mipams.jumbf.demo.entities.privacy_security.replacement;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.FileParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;

public class FileParamParser implements ParamParserInterface {

    public ParamHandlerInterface populateParamFromRequest(ObjectNode input) throws MipamsException {

        FileParamHandler paramHandler = new FileParamHandler();

        return paramHandler;
    }
}

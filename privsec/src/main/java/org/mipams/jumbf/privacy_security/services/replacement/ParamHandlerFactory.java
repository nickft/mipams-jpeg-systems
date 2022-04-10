package org.mipams.jumbf.privacy_security.services.replacement;

import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.replacement.AppParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.BoxParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.FileParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.entities.replacement.RoiParamHandler;

import org.springframework.stereotype.Component;

@Component
public class ParamHandlerFactory {
    public ParamHandlerInterface getParamHandler(ReplacementType replacementType) throws MipamsException {

        ParamHandlerInterface result = null;

        switch (replacementType) {
            case BOX:
                result = new BoxParamHandler();
                break;
            case APP:
                result = new AppParamHandler();
                break;
            case ROI:
                result = new RoiParamHandler();
                break;
            case FILE:
                result = new FileParamHandler();
                break;
            default:
                throw new MipamsException();
        }

        return result;
    }
}

package org.mipams.jumbf.privacy_security.services.replacement;

import org.mipams.jumbf.privacy_security.entities.replacement.AppParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.BoxParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.EmptyParamHandler;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.entities.replacement.RoiParamHandler;

import org.springframework.stereotype.Component;

@Component
public class ParamHandlerFactory {
    public ParamHandlerInterface getParamHandler(ReplacementType replacementType, long remainingBytes) {

        ParamHandlerInterface result = null;

        if (remainingBytes == 0) {
            result = new EmptyParamHandler();
        } else {

            switch (replacementType) {
                case APP:
                    result = new AppParamHandler();
                    break;
                case ROI:
                    result = new RoiParamHandler();
                    break;
                case BOX:
                default:
                    result = new BoxParamHandler();
                    break;
            }
        }

        return result;
    }
}

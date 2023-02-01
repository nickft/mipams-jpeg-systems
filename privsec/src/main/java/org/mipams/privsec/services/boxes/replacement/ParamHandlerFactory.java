package org.mipams.privsec.services.boxes.replacement;

import org.mipams.privsec.entities.replacement.AppParamHandler;
import org.mipams.privsec.entities.replacement.BoxParamHandler;
import org.mipams.privsec.entities.replacement.FileParamHandler;
import org.mipams.privsec.entities.replacement.ParamHandlerInterface;
import org.mipams.privsec.entities.replacement.ReplacementType;
import org.mipams.privsec.entities.replacement.RoiParamHandler;

import org.springframework.stereotype.Component;

@Component
public class ParamHandlerFactory {
    public ParamHandlerInterface getParamHandler(ReplacementType replacementType) {

        ParamHandlerInterface result = null;

        switch (replacementType) {
            case APP:
                result = new AppParamHandler();
                break;
            case ROI:
                result = new RoiParamHandler();
                break;
            case FILE:
                result = new FileParamHandler();
                break;
            case BOX:
            default:
                result = new BoxParamHandler();
                break;
        }

        return result;
    }
}

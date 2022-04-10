package org.mipams.jumbf.privacy_security.services.replacement;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataBoxHandlerFactory {

    @Autowired
    BoxReplacementHandler boxReplacementHandler;

    @Autowired
    AppReplacementHandler appReplacementHandler;

    @Autowired
    RoiReplacementHandler roiReplacementHandler;

    public DataBoxHandler getDataBoxHandlerFromType(ReplacementType replacementType) throws MipamsException {

        DataBoxHandler result = null;

        switch (replacementType) {
            case BOX:
                result = boxReplacementHandler;
                break;
            case APP:
                result = appReplacementHandler;
                break;
            case ROI:
            case FILE:
                result = roiReplacementHandler;
                break;
            default:
                throw new MipamsException(replacementType.getType() + " is not supported");
        }

        return result;
    }
}

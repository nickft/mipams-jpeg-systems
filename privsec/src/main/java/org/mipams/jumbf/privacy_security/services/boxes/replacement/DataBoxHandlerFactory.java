package org.mipams.jumbf.privacy_security.services.boxes.replacement;

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

    public DataBoxHandler getDataBoxHandlerFromType(ReplacementType replacementType) {

        DataBoxHandler result = null;

        switch (replacementType) {
            case APP:
                result = appReplacementHandler;
                break;
            case ROI:
            case FILE:
                result = roiReplacementHandler;
                break;
            case BOX:
            default:
                result = boxReplacementHandler;
                break;
        }

        return result;
    }
}

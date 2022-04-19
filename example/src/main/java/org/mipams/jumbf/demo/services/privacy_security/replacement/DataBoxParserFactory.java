package org.mipams.jumbf.demo.services.privacy_security.replacement;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataBoxParserFactory {

    @Autowired
    BoxReplacementDataBoxParser boxReplacementDataBoxParser;

    @Autowired
    AppReplacementDataBoxParser appReplacementDataBoxParser;

    @Autowired
    RoiReplacementDataBoxParser roiReplacementDataBoxParser;

    public DataBoxParser getDataBoxParserFromType(ReplacementType replacementType) throws MipamsException {

        DataBoxParser result = null;

        switch (replacementType) {
            case BOX:
                result = boxReplacementDataBoxParser;
                break;
            case APP:
                result = appReplacementDataBoxParser;
                break;
            case ROI:
            case FILE:
                result = roiReplacementDataBoxParser;
                break;
            default:
                throw new MipamsException(replacementType.getType() + " is not supported");
        }

        return result;
    }
}

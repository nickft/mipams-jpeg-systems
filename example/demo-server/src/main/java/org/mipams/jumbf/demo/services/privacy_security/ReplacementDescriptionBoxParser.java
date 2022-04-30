package org.mipams.jumbf.demo.services.privacy_security;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.entities.privacy_security.replacement.ParamParserInterface;
import org.mipams.jumbf.demo.services.core.BmffBoxParser;
import org.mipams.jumbf.demo.services.privacy_security.replacement.ParamParserFactory;

import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementDescriptionBoxParser extends BmffBoxParser<ReplacementDescriptionBox> {

    @Autowired
    ParamParserFactory paramParserFactory;

    @Override
    protected final void populateBox(ReplacementDescriptionBox box, ObjectNode input) throws MipamsException {
        String typeAsString = input.get("replacementType").asText();

        ReplacementType replacementType = ReplacementType.getTypeFromString(typeAsString);

        box.setReplacementTypeId(replacementType.getId());

        boolean isAutoApply = input.get("auto-apply").asBoolean();
        box.setAutoApply(isAutoApply);

        ParamParserInterface paramHandler = paramParserFactory.getParamParser(replacementType);

        box.setParamHandler(paramHandler.populateParamFromRequest(input));
    }

    @Override
    protected ReplacementDescriptionBox initializeBox() {
        return new ReplacementDescriptionBox();
    }
}

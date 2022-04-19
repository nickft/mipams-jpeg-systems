package org.mipams.jumbf.demo.services.privacy_security;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.core.ContentBoxParser;
import org.mipams.jumbf.demo.services.privacy_security.replacement.DataBoxParser;
import org.mipams.jumbf.demo.services.privacy_security.replacement.DataBoxParserFactory;

import org.mipams.jumbf.privacy_security.entities.ReplacementBox;
import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.ReplacementBoxService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementBoxParser implements ContentBoxParser {

    @Autowired
    ReplacementBoxService replacementBoxService;

    @Autowired
    ReplacementDescriptionBoxParser replacementDescriptionBoxParser;

    @Autowired
    DataBoxParserFactory dataBoxParserFactory;

    @Override
    public ReplacementBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        ReplacementBox replacementBox = new ReplacementBox();

        ObjectNode replacementDescriptionNode = (ObjectNode) inputNode.get("replacementDescription");
        ReplacementDescriptionBox descriptionBox = replacementDescriptionBoxParser
                .discoverBoxFromRequest(replacementDescriptionNode);

        replacementBox.setDescriptionBox(descriptionBox);

        ReplacementType replacementType = replacementBox.getReplacementType();
        DataBoxParser dataBoxParser = dataBoxParserFactory.getDataBoxParserFromType(replacementType);

        List<BmffBox> contentBoxList = dataBoxParser.discoverDataBoxFromRequest(inputNode);

        replacementBox.setReplacementDataBoxList(contentBoxList);

        return replacementBox;
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return replacementBoxService.getServiceMetadata();
    }
}

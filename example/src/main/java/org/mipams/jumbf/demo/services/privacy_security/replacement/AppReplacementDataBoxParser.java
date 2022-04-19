package org.mipams.jumbf.demo.services.privacy_security.replacement;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.core.BinaryDataBoxParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppReplacementDataBoxParser implements DataBoxParser {

    @Autowired
    BinaryDataBoxParser binaryDataBoxParser;

    @Override
    public List<BmffBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException {

        ObjectNode contentNode = (ObjectNode) inputNode.get("content");

        BinaryDataBox binaryDataBox = binaryDataBoxParser.discoverBoxFromRequest(contentNode);
        return List.of(binaryDataBox);
    }
}

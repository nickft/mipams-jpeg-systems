package org.mipams.jumbf.demo.services.privacy_security;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.ContentTypeParser;
import org.mipams.jumbf.demo.services.core.BinaryDataBoxParser;

import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.services.content_types.ProtectionContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionBoxParser implements ContentTypeParser {

    @Autowired
    ProtectionContentType protectionContentType;

    @Autowired
    ProtectionDescriptionBoxParser protectionDescriptionBoxParser;

    @Autowired
    BinaryDataBoxParser binaryDataBoxParser;

    @Override
    public List<BmffBox> discoverContentBoxesFromRequest(ObjectNode inputNode) throws MipamsException {
        ObjectNode protectionDescriptionNode = (ObjectNode) inputNode.get("protectionDescription");
        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxParser
                .discoverBoxFromRequest(protectionDescriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) inputNode.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxParser.discoverBoxFromRequest(binaryDataNode);

        return List.of(protectionDescriptionBox, binaryDataBox);
    }

    @Override
    public String getContentTypeUuid() {
        return protectionContentType.getContentTypeUuid();
    }

}

package org.mipams.jumbf.demo.services.privacy_security;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.core.BinaryDataBoxParser;
import org.mipams.jumbf.demo.services.core.ContentBoxParser;
import org.mipams.jumbf.privacy_security.entities.ProtectionBox;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.services.ProtectionBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionBoxParser implements ContentBoxParser {

    @Autowired
    ProtectionBoxService protectionBoxService;

    @Autowired
    ProtectionDescriptionBoxParser protectionDescriptionBoxParser;

    @Autowired
    BinaryDataBoxParser binaryDataBoxParser;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return protectionBoxService.getServiceMetadata();
    }

    @Override

    public ProtectionBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        ProtectionBox protectionBox = new ProtectionBox();

        ObjectNode protectionDescriptionNode = (ObjectNode) inputNode.get("protectionDescription");
        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxParser
                .discoverBoxFromRequest(protectionDescriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) inputNode.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxParser.discoverBoxFromRequest(binaryDataNode);

        protectionBox.setProtectionDescriptionBox(protectionDescriptionBox);
        protectionBox.setBinaryDataBox(binaryDataBox);

        return protectionBox;
    }

}

package org.mipams.jumbf.demo.services.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.EmbeddedFileBox;
import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.EmbeddedFileBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileBoxParser implements ContentBoxParser {

    @Autowired
    EmbeddedFileBoxService embeddedFileBoxService;

    @Autowired
    EmbeddedFileDescriptionBoxParser embeddedFileDescriptionBoxParser;

    @Autowired
    BinaryDataBoxParser binaryDataBoxParser;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return embeddedFileBoxService.getServiceMetadata();
    }

    @Override
    public EmbeddedFileBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();

        ObjectNode descriptionNode = (ObjectNode) inputNode.get("embeddedFileDescription");
        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = embeddedFileDescriptionBoxParser
                .discoverBoxFromRequest(descriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) inputNode.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxParser.discoverBoxFromRequest(binaryDataNode);

        binaryDataBox.setReferencedExternally(embeddedFileDescriptionBox.isContentReferencedExternally());
        binaryDataBox.updateBmffHeadersBasedOnBox();

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
        embeddedFileBox.setBinaryDataBox(binaryDataBox);

        return embeddedFileBox;
    }

}

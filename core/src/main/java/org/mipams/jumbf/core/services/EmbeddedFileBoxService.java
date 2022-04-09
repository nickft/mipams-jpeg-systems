package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.EmbeddedFileBox;
import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileBoxService implements ContentBoxService<EmbeddedFileBox> {

    @Autowired
    EmbeddedFileDescriptionBoxService embeddedFileDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.EmbeddedFileBox.getServiceMetadata();
    }

    @Override
    public void writeToJumbfFile(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        embeddedFileDescriptionBoxService.writeToJumbfFile(embeddedFileBox.getDescriptionBox(), fileOutputStream);
        binaryDataBoxService.writeToJumbfFile(embeddedFileBox.getBinaryDataBox(), fileOutputStream);
    }

    @Override
    public EmbeddedFileBox parseFromJumbfFile(InputStream inputStream) throws MipamsException {

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBoxService.parseFromJumbfFile(inputStream));
        embeddedFileBox.setBinaryDataBox(binaryDataBoxService.parseFromJumbfFile(inputStream));

        return embeddedFileBox;
    }

    public String getFileUrlFromBox(EmbeddedFileBox embeddedFileBox) throws MipamsException {

        if (embeddedFileBox.getDescriptionBox().isContentReferencedExternally()) {
            return embeddedFileBox.getDescriptionBox().getFileName();
        } else {
            return CoreUtils.parseStringFromFile(embeddedFileBox.getBinaryDataBox().getFileUrl());
        }
    }

    @Override
    public EmbeddedFileBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();

        ObjectNode descriptionNode = (ObjectNode) inputNode.get("embeddedFileDescription");
        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = embeddedFileDescriptionBoxService
                .discoverBoxFromRequest(descriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) inputNode.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxService.discoverBoxFromRequest(binaryDataNode);

        binaryDataBox.setReferencedExternally(embeddedFileDescriptionBox.isContentReferencedExternally());
        binaryDataBox.updateXTHeadersBasedOnBox();

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
        embeddedFileBox.setBinaryDataBox(binaryDataBox);

        return embeddedFileBox;
    }
}
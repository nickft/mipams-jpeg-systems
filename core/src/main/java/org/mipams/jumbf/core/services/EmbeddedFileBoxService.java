package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.EmbeddedFileBox;
import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmbeddedFileBoxService extends XTBoxService<EmbeddedFileBox> {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedFileBoxService.class);

    @Autowired
    EmbeddedFileDescriptionBoxService embeddedFileDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    protected EmbeddedFileBox initializeBox() throws MipamsException {
        return new EmbeddedFileBox();
    }

    @Override
    protected void populateBox(EmbeddedFileBox embeddedFileBox, ObjectNode input) throws MipamsException {

        ObjectNode descriptionNode = (ObjectNode) input.get("description");
        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = embeddedFileDescriptionBoxService
                .discoverXTBoxFromRequest(descriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) input.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxService.discoverXTBoxFromRequest(binaryDataNode);
        binaryDataBox.setReferencedExternally(embeddedFileDescriptionBox.isContentReferencedExternally());

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
        embeddedFileBox.setBinaryDataBox(binaryDataBox);
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        embeddedFileDescriptionBoxService.writeToJumbfFile(embeddedFileBox.getDescriptionBox(), fileOutputStream);
        binaryDataBoxService.writeToJumbfFile(embeddedFileBox.getBinaryDataBox(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(EmbeddedFileBox embeddedFileBox, InputStream inputStream)
            throws MipamsException {
        logger.debug("Embedded File box");

        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBoxService.parseFromJumbfFile(inputStream));
        embeddedFileBox.setBinaryDataBox(binaryDataBoxService.parseFromJumbfFile(inputStream));
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.EmbeddedFileBox.getType();
    }

    public String getFileUrlFromBox(EmbeddedFileBox embeddedFileBox) throws MipamsException {

        if (embeddedFileBox.getDescriptionBox().isContentReferencedExternally()) {
            return embeddedFileBox.getDescriptionBox().getFileName();
        } else {
            return CoreUtils.parseStringFromFile(embeddedFileBox.getBinaryDataBox().getFileUrl());
        }
    }
}
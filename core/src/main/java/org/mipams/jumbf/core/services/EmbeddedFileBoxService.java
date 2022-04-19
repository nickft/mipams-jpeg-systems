package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.EmbeddedFileBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
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

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        EmbeddedFileBox box = new EmbeddedFileBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    public void writeToJumbfFile(EmbeddedFileBox embeddedFileBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        embeddedFileDescriptionBoxService.writeToJumbfFile(embeddedFileBox.getDescriptionBox(), fileOutputStream);
        binaryDataBoxService.writeToJumbfFile(embeddedFileBox.getBinaryDataBox(), fileOutputStream);
    }

    @Override
    public EmbeddedFileBox parseFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();

        embeddedFileBox.setDescriptionBox(
                embeddedFileDescriptionBoxService.parseFromJumbfFile(inputStream, availableBytesForBox));
        embeddedFileBox.setBinaryDataBox(binaryDataBoxService.parseFromJumbfFile(inputStream, availableBytesForBox));

        boolean fileReference = embeddedFileBox.getDescriptionBox().isContentReferencedExternally();
        embeddedFileBox.getBinaryDataBox().setReferencedExternally(fileReference);

        return embeddedFileBox;
    }

    public String getFileUrlFromBox(EmbeddedFileBox embeddedFileBox) throws MipamsException {

        if (embeddedFileBox.getDescriptionBox().isContentReferencedExternally()) {
            return CoreUtils.parseStringFromFile(embeddedFileBox.getBinaryDataBox().getFileUrl());
        } else {
            return embeddedFileBox.getDescriptionBox().getFileName();
        }
    }
}
package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BinaryDataBoxService;
import org.mipams.jumbf.core.services.ContentBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ProtectionBox;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionBoxService implements ContentBoxService<ProtectionBox> {

    @Autowired
    ProtectionDescriptionBoxService protectionDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ProtectionBox box = new ProtectionBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    public void writeToJumbfFile(ProtectionBox protectionBox, FileOutputStream output) throws MipamsException {
        protectionDescriptionBoxService.writeToJumbfFile(protectionBox.getProtectionDescriptionBox(), output);
        binaryDataBoxService.writeToJumbfFile(protectionBox.getBinaryDataBox(), output);
    }

    @Override
    public ProtectionBox parseFromJumbfFile(InputStream input, long availableBytesForBox) throws MipamsException {
        ProtectionBox protectionBox = new ProtectionBox();

        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxService.parseFromJumbfFile(input,
                availableBytesForBox);
        protectionBox.setProtectionDescriptionBox(protectionDescriptionBox);

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(input, availableBytesForBox);
        protectionBox.setBinaryDataBox(binaryDataBox);

        return protectionBox;
    }
}

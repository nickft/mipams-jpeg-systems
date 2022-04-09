package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BinaryDataBoxService;
import org.mipams.jumbf.core.services.ContentBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ProtectionBox;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionBoxService implements ContentBoxService<ProtectionBox> {

    @Autowired
    ProtectionDescriptionBoxService protectionDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.ProtectionBox.getServiceMetadata();
    }

    @Override
    public ProtectionBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        ProtectionBox protectionBox = new ProtectionBox();

        ObjectNode protectionDescriptionNode = (ObjectNode) inputNode.get("protectionDescription");
        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxService
                .discoverBoxFromRequest(protectionDescriptionNode);

        ObjectNode binaryDataNode = (ObjectNode) inputNode.get("content");
        BinaryDataBox binaryDataBox = binaryDataBoxService.discoverBoxFromRequest(binaryDataNode);

        protectionBox.setProtectionDescriptionBox(protectionDescriptionBox);
        protectionBox.setBinaryDataBox(binaryDataBox);

        return protectionBox;
    }

    @Override
    public void writeToJumbfFile(ProtectionBox protectionBox, FileOutputStream output) throws MipamsException {
        protectionDescriptionBoxService.writeToJumbfFile(protectionBox.getProtectionDescriptionBox(), output);
        binaryDataBoxService.writeToJumbfFile(protectionBox.getBinaryDataBox(), output);
    }

    @Override
    public ProtectionBox parseFromJumbfFile(InputStream input) throws MipamsException {
        ProtectionBox protectionBox = new ProtectionBox();

        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxService.parseFromJumbfFile(input);
        protectionBox.setProtectionDescriptionBox(protectionDescriptionBox);

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(input);
        protectionBox.setBinaryDataBox(binaryDataBox);

        return protectionBox;
    }
}

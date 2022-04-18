package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.ContentBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.ReplacementBox;
import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.replacement.DataBoxHandler;
import org.mipams.jumbf.privacy_security.services.replacement.DataBoxHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementBoxService implements ContentBoxService<ReplacementBox> {

    @Autowired
    ReplacementDescriptionBoxService replacementDescriptionBoxService;

    @Autowired
    DataBoxHandlerFactory dataBoxHandlerFactory;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ReplacementBox box = new ReplacementBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    public ReplacementBox discoverBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        ReplacementBox replacementBox = new ReplacementBox();

        ObjectNode replacementDescriptionNode = (ObjectNode) inputNode.get("replacementDescription");
        ReplacementDescriptionBox descriptionBox = replacementDescriptionBoxService
                .discoverBoxFromRequest(replacementDescriptionNode);

        replacementBox.setDescriptionBox(descriptionBox);

        ReplacementType replacementType = replacementBox.getReplacementType();
        DataBoxHandler dataBoxHandler = dataBoxHandlerFactory.getDataBoxHandlerFromType(replacementType);

        List<BmffBox> contentBoxList = dataBoxHandler.discoverDataBoxFromRequest(inputNode);

        replacementBox.setReplacementDataBoxList(contentBoxList);

        return replacementBox;
    }

    @Override
    public void writeToJumbfFile(ReplacementBox replacementBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        replacementDescriptionBoxService.writeToJumbfFile(replacementBox.getDescriptionBox(), fileOutputStream);

        ReplacementType replacementType = replacementBox.getReplacementType();
        DataBoxHandler dataBoxHandler = dataBoxHandlerFactory.getDataBoxHandlerFromType(replacementType);

        dataBoxHandler.writeDataBoxToJumbfFile(replacementBox.getReplacementDataBoxList(), fileOutputStream);
    }

    @Override
    public ReplacementBox parseFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        ReplacementBox replacementBox = new ReplacementBox();
        ReplacementDescriptionBox descriptionBox = replacementDescriptionBoxService.parseFromJumbfFile(inputStream,
                availableBytesForBox);

        availableBytesForBox -= descriptionBox.getBoxSize();

        replacementBox.setDescriptionBox(descriptionBox);

        ReplacementType replacementType = replacementBox.getReplacementType();
        DataBoxHandler dataBoxHandler = dataBoxHandlerFactory.getDataBoxHandlerFromType(replacementType);

        List<BmffBox> contentBoxList = dataBoxHandler.parseDataBoxFromJumbfFile(inputStream, availableBytesForBox);
        replacementBox.setReplacementDataBoxList(contentBoxList);

        return replacementBox;
    }

}

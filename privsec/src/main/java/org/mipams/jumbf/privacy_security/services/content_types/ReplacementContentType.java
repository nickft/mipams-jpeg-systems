package org.mipams.jumbf.privacy_security.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.content_types.ContentTypeService;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.boxes.ReplacementDescriptionBoxService;
import org.mipams.jumbf.privacy_security.services.boxes.replacement.DataBoxHandler;
import org.mipams.jumbf.privacy_security.services.boxes.replacement.DataBoxHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementContentType implements ContentTypeService {

    @Autowired
    ReplacementDescriptionBoxService replacementDescriptionBoxService;

    @Autowired
    DataBoxHandlerFactory dataBoxHandlerFactory;

    @Override
    public String getContentTypeUuid() {
        return "DC28B95F-B68A-498E-8064-0FCA845D6B0E";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        ReplacementDescriptionBox replacementDescriptionBox = replacementDescriptionBoxService.parseFromJumbfFile(
                inputStream, availableBytesForBox);

        availableBytesForBox -= replacementDescriptionBox.getBoxSize();

        ReplacementType replacementType = getReplacementType(replacementDescriptionBox);
        DataBoxHandler dataBoxHandler = dataBoxHandlerFactory.getDataBoxHandlerFromType(replacementType);

        List<BmffBox> replacementDataBoxList = dataBoxHandler.parseDataBoxFromJumbfFile(inputStream,
                availableBytesForBox);

        List<BmffBox> contentBoxList = new ArrayList<>();
        contentBoxList.add(replacementDescriptionBox);
        contentBoxList.addAll(replacementDataBoxList);

        return contentBoxList;
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {

        ReplacementDescriptionBox replacementDescriptionBox = (ReplacementDescriptionBox) inputBox.get(0);

        replacementDescriptionBoxService.writeToJumbfFile(replacementDescriptionBox, outputStream);

        ReplacementType replacementType = getReplacementType(replacementDescriptionBox);
        DataBoxHandler dataBoxHandler = dataBoxHandlerFactory.getDataBoxHandlerFromType(replacementType);

        List<BmffBox> replacementDataBoxList = new ArrayList<>(inputBox);
        replacementDataBoxList.remove(0);

        dataBoxHandler.writeDataBoxToJumbfFile(replacementDataBoxList, outputStream);
    }

    ReplacementType getReplacementType(ReplacementDescriptionBox replacementDescriptionBox) throws MipamsException {
        return ReplacementType
                .getTypeFromId(replacementDescriptionBox.getReplacementTypeId());
    }

}

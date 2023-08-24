package org.mipams.jpegsnack.services.content_types;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.mipams.jpegsnack.entities.InstructionSetBox;
import org.mipams.jpegsnack.entities.JpegSnackDescriptionBox;
import org.mipams.jpegsnack.entities.JpegSnackParseMetadata;
import org.mipams.jpegsnack.entities.ObjectMetadataBox;
import org.mipams.jpegsnack.services.boxes.InstructionSetBoxService;
import org.mipams.jpegsnack.services.boxes.JpegSnackDescriptionBoxService;
import org.mipams.jpegsnack.services.boxes.ObjectMetadataBoxService;
import org.mipams.jpegsnack.util.JpegSnackException;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.content_types.ContentTypeService;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegSnackContentType implements ContentTypeService {

    @Autowired
    JpegSnackDescriptionBoxService jpegSnackDescriptionBoxService;

    @Autowired
    InstructionSetBoxService instructionSetBoxService;

    @Autowired
    ObjectMetadataBoxService objectMetadataBoxService;

    @Override
    public String getContentTypeUuid() {
        return "16AD91E0-A37F-11EB-9D0D-0800200C9A66";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream is, ParseMetadata parseMetadata)
            throws MipamsException {

        List<BmffBox> contentBoxes = new ArrayList<>();

        JpegSnackDescriptionBox jsdb = jpegSnackDescriptionBoxService.parseFromJumbfFile(is, parseMetadata);
        contentBoxes.add(jsdb);

        Long remainingBytes = parseMetadata.getAvailableBytesForBox() > 0
                ? parseMetadata.getAvailableBytesForBox() - jsdb.getBoxSize()
                : parseMetadata.getAvailableBytesForBox();

        JpegSnackParseMetadata jpegSnackParseMetadata = new JpegSnackParseMetadata();
        jpegSnackParseMetadata.setAvailableBytesForBox(remainingBytes);
        jpegSnackParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());
        jpegSnackParseMetadata.setNoOfObjects(jsdb.getCompositions().get(0).getNoOfObjects());

        InstructionSetBox issb = instructionSetBoxService.parseFromJumbfFile(is, jpegSnackParseMetadata);
        contentBoxes.add(issb);

        remainingBytes = parseMetadata.getAvailableBytesForBox() > 0
                ? parseMetadata.getAvailableBytesForBox() - issb.getBoxSize()
                : parseMetadata.getAvailableBytesForBox();

        jpegSnackParseMetadata.setAvailableBytesForBox(remainingBytes);
        jpegSnackParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

        for (int i = 0; i < jpegSnackParseMetadata.getNoOfObjects(); i++) {
            ObjectMetadataBox obmb = objectMetadataBoxService.parseFromJumbfFile(is, jpegSnackParseMetadata);
            contentBoxes.add(obmb);
        }

        validateContentBoxes(contentBoxes);

        return contentBoxes;
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> providedContentBoxes, OutputStream os)
            throws MipamsException {

        validateContentBoxes(providedContentBoxes);

        List<BmffBox> contentBoxes = new ArrayList<>(providedContentBoxes);

        JpegSnackDescriptionBox jsdb = (JpegSnackDescriptionBox) contentBoxes.remove(0);
        jpegSnackDescriptionBoxService.writeToJumbfFile(jsdb, os);

        InstructionSetBox issb = (InstructionSetBox) contentBoxes.remove(0);
        instructionSetBoxService.writeToJumbfFile(issb, os);

        for (BmffBox bmffBox : contentBoxes) {
            ObjectMetadataBox obmb = (ObjectMetadataBox) bmffBox;
            objectMetadataBoxService.writeToJumbfFile(obmb, os);
        }
    }

    private void validateContentBoxes(List<BmffBox> contentBoxes) throws MipamsException {

        JpegSnackDescriptionBox jsdb;
        InstructionSetBox issb;

        try {
            jsdb = (JpegSnackDescriptionBox) contentBoxes.get(0);
            issb = (InstructionSetBox) contentBoxes.get(1);
        } catch (Exception e) {
            throw new JpegSnackException(
                    "Failed to instantiate the necessary JPEG Snack Description box and Instruction Set box", e);
        }

        if (jsdb.getVersion() != 1) {
            throw new JpegSnackException("Currently only version 1 is supported");
        }

        int instructionSetToggle = issb.getLtyp();
        issb.applyInternalBoxFieldsBasedOnExistingData();

        if (issb.getLtyp() != instructionSetToggle) {
            throw new JpegSnackException(
                    String.format("Invalid Instruction Set Toggle. Expected: %d Found: %d", issb.getLtyp(),
                            instructionSetToggle));
        }

        int noOfObjects = jsdb.getCompositions().get(0).getNoOfObjects();

        if (noOfObjects != issb.getInstructionParameters().size()) {
            throw new JpegSnackException(
                    String.format("Inconsistency in the number of objects (%d) and instruction parameters (%d)",
                            noOfObjects, issb.getInstructionParameters().size()));
        }

        if (noOfObjects != contentBoxes.size() - 2) {
            throw new JpegSnackException(
                    String.format("Inconsistency in the number of objects (%d) and existing object metadata boxes (%d)",
                            noOfObjects, contentBoxes.size() - 2));
        }

        List<Integer> discoveredObjectIds = new ArrayList<>();
        String mediaType = null;
        for (int i = 2; i < contentBoxes.size(); i++) {

            ObjectMetadataBox obmb = (ObjectMetadataBox) contentBoxes.get(i);

            if (obmb.getNoOfMedia() < 0) {
                throw new JpegSnackException(
                        "Corrupted Object Metadata box. Number of media field shall be greater than zero");
            }

            int objectMetadataBoxToggle = obmb.getToggle();
            obmb.applyInternalBoxFieldsBasedOnExistingData();

            if (obmb.getToggle() != objectMetadataBoxToggle) {
                throw new JpegSnackException(
                        String.format("Invalid Object Metadata Toggle. Expected: %d Found: %d", obmb.getToggle(),
                                objectMetadataBoxToggle));
            }

            if (mediaType == null) {
                mediaType = obmb.getMediaType();
            } else {
                if (mediaType != obmb.getMediaType()) {
                    throw new JpegSnackException(
                            String.format("Inconsistent media type in object metadata list. Expected %s, found %s",
                                    mediaType, obmb.getMediaType()));
                }
            }

            if (obmb.getId() == 0) {
                throw new JpegSnackException("Object metadata box id shall be a non-zero integer.");
            }

            if (obmb.getOpacity() != null && (obmb.getOpacity() > 1 || obmb.getOpacity() < 0)) {
                throw new JpegSnackException(
                        String.format("Invalid opacity value for Object Metadata. It shall be between 0-1, found %f",
                                obmb.getOpacity()));
            }

            if (discoveredObjectIds.contains(obmb.getId())) {
                throw new JpegSnackException("Object metadata box id %d shall be unique in a JPEG Snack file");
            }
        }
    }

}

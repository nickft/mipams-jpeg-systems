package org.mipams.jpeg360.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.mipams.jpeg360.entities.Jpeg360AcceleratedRoi;

public class Jpeg360AcceleratedRoiValidator extends Jpeg360AbstractValidator<Jpeg360AcceleratedRoi> {
    public Jpeg360AcceleratedRoiValidator(Model jpeg360Model, Map<String, Resource> subjectNameToResourceMap) {
        super(jpeg360Model, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return Jpeg360AcceleratedRoiProperty.getKeysAsList();
    }

    @Override
    protected Jpeg360AcceleratedRoi initializeJpeg360Element() {
        return new Jpeg360AcceleratedRoi();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return Jpeg360AcceleratedRoiProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(Jpeg360AcceleratedRoi acceleratedRoi, Map<String, String> viewportProperties)
            throws Exception {

        String roiNumber = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.ROI_NUMBER.getKey(), "");
        String roiPosX = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.ROI_POS_X.getKey(), "");
        String roiPosY = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.ROI_POS_Y.getKey(), "");
        String blockWidth = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.BLOCK_WIDTH.getKey(), "");
        String blockHeight = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.BLOCK_HEIGHT.getKey(), "");
        String offsetX = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.OFFSET_X.getKey(), "");
        String offsetY = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.OFFSET_Y.getKey(), "");
        String widthInBlocks = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.WIDTH_IN_BLOCKS.getKey(),
                "");
        String heightInBlocks = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.HEIGHT_IN_BLOCKS.getKey(),
                "");

        String associatedViewport = viewportProperties
                .getOrDefault(Jpeg360AcceleratedRoiProperty.ASSOCIATED_VIEWPORT.getKey(), "");
        String boxReference = viewportProperties.getOrDefault(Jpeg360AcceleratedRoiProperty.BOX_REFERENCE.getKey(), "");

        if (roiNumber.isBlank()) {
            throw new Exception("RoiNumber shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setRoiNumber(Integer.parseInt(roiNumber));

        if (roiPosX.isBlank()) {
            throw new Exception("roiPosX shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setRoiPosX(Integer.parseInt(roiPosX));

        if (roiPosY.isBlank()) {
            throw new Exception("roiPosY shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setRoiPosY(Integer.parseInt(roiPosY));

        if (blockWidth.isBlank()) {
            throw new Exception("blockWidth shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setBlockWidth(Integer.parseInt(blockWidth));

        if (blockHeight.isBlank()) {
            throw new Exception("blockHeight shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setBlockHeight(Integer.parseInt(blockHeight));

        if (!offsetX.isBlank()) {
            acceleratedRoi.setOffsetX(Integer.parseInt(offsetX));
        }

        if (!offsetY.isBlank()) {
            acceleratedRoi.setOffsetY(Integer.parseInt(offsetY));
        }

        if (widthInBlocks.isBlank()) {
            throw new Exception("widthInBlocks shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setWidthInBlocks(Integer.parseInt(widthInBlocks));

        if (heightInBlocks.isBlank()) {
            throw new Exception("heightInBlocks shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setHeightInBlocks(Integer.parseInt(heightInBlocks));

        if (associatedViewport.isBlank()) {
            throw new Exception(
                    "associatedViewport shall be provided in JPEG 360 Schema for Accelerated ROIs");
        }
        acceleratedRoi.setAssociatedViewport(Integer.parseInt(associatedViewport));

        if (boxReference.isBlank()) {
            throw new Exception(
                    "boxReference shall be provided in JPEG 360 Schema for Accelerated ROIs except for global CTE re-ordering");
        }
        acceleratedRoi.setBoxReference(boxReference);
    }

    @Override
    protected void validateElementBasedOnSchemaProperties(Jpeg360AcceleratedRoi element,
            List<String> propertiesYetNotFoundInStructure) throws Exception {
        super.validateElementBasedOnSchemaProperties(element, propertiesYetNotFoundInStructure);
        validateMetadataValues(element);
    }

    private void validateMetadataValues(Jpeg360AcceleratedRoi element) throws Exception {
        if (element.getRoiNumber() < 0) {
            throw new Exception(
                    String.format("ROINumber shall be greater than zero. Found %d", element.getRoiNumber()));
        }
    }

    enum Jpeg360AcceleratedRoiProperty {
        ROI_NUMBER("ROINumber", PropertyType.INTEGER),
        ROI_POS_X("ROIPosX", PropertyType.INTEGER),
        ROI_POS_Y("ROIPosY", PropertyType.INTEGER),
        BLOCK_WIDTH("BlockWidth", PropertyType.INTEGER),
        BLOCK_HEIGHT("BlockHeight", PropertyType.INTEGER),
        WIDTH_IN_BLOCKS("WidthInBlocks", PropertyType.INTEGER),
        HEIGHT_IN_BLOCKS("HeightInBlocks", PropertyType.INTEGER),
        OFFSET_X("OffsetX", PropertyType.INTEGER),
        OFFSET_Y("OffsetY", PropertyType.INTEGER),
        ASSOCIATED_VIEWPORT("AssociatedViewport", PropertyType.INTEGER),
        BOX_REFERENCE("BoxReference", PropertyType.STRING);

        private String key;
        private PropertyType type;

        Jpeg360AcceleratedRoiProperty(String key, PropertyType type) {
            this.key = key;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public PropertyType getType() {
            return type;
        }

        public static List<String> getKeysAsList() {
            List<String> result = new ArrayList<>();
            for (Jpeg360AcceleratedRoiProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static Jpeg360AcceleratedRoiProperty getPropertyFromString(String value) throws Exception {
            for (Jpeg360AcceleratedRoiProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(
                    String.format("Property %s is not supported for Jpeg 360 Accelerated ROI schema", value));
        }
    }
}

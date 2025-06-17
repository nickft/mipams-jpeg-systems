package org.mipams.jpeg360.entities.generator;

import org.mipams.jpeg360.entities.Jpeg360AcceleratedRoi;
import org.mipams.jpeg360.entities.validator.PropertyType;

public class Jpeg360AcceleraterRoiGenerator extends Jpeg360AbstractGenerator<Jpeg360AcceleratedRoi> {

    @Override
    public String schemaToRdfXml(Jpeg360AcceleratedRoi jpeg360Element) throws Exception {
        StringBuilder acceleratedRoiSchema = new StringBuilder(addResourceOpeningTag());

        acceleratedRoiSchema.append(addName("JPEG360AcceleratedROI"));
        acceleratedRoiSchema.append(addContentsOpeningTag(false));

        acceleratedRoiSchema.append(addSchemaProperty("ROINumber", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("ROIPosX", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("ROIPosY", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("BlockWidth", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("BlockHeight", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("OffsetX", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("OffsetY", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("WidthInBlocks", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("HeightInBlocks", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("AssociatedViewport", PropertyType.INTEGER));
        acceleratedRoiSchema.append(addSchemaProperty("BoxReference", PropertyType.STRING));

        acceleratedRoiSchema.append(addContentsClosingTag(false));
        acceleratedRoiSchema.append(addResourceClosingTag());
        return acceleratedRoiSchema.toString();
    }

    @Override
    public String metadataToRdfXml(Jpeg360AcceleratedRoi acceleratedRoi) throws Exception {
        StringBuilder acceleratedRoiElement = new StringBuilder(addResourceOpeningTag());

        acceleratedRoiElement.append(addName("JPEG360AcceleratedROI"));
        acceleratedRoiElement.append(addContentsOpeningTag(true));

        acceleratedRoiElement.append(addResourceOpeningTag());
        acceleratedRoiElement.append(String.format("<umf:id>%d</umf:id>", acceleratedRoi.getUmdId()));
        acceleratedRoiElement.append(addContentsOpeningTag(false));

        acceleratedRoiElement
                .append(addMetadataProperty("ROINumber",
                        Integer.toString(acceleratedRoi.getRoiNumber())));
        acceleratedRoiElement
                .append(addMetadataProperty("ROIPosX", Integer.toString(acceleratedRoi.getRoiPosX())));
        acceleratedRoiElement
                .append(addMetadataProperty("ROIPosY", Integer.toString(acceleratedRoi.getRoiPosY())));
        acceleratedRoiElement
                .append(addMetadataProperty("BlockWidth",
                        Integer.toString(acceleratedRoi.getBlockWidth())));
        acceleratedRoiElement
                .append(addMetadataProperty("BlockHeight",
                        Integer.toString(acceleratedRoi.getBlockHeight())));

        if (acceleratedRoi.getOffsetX() != null) {
            acceleratedRoiElement
                    .append(addMetadataProperty("OffsetX",
                            Integer.toString(acceleratedRoi.getOffsetX())));
        }

        if (acceleratedRoi.getOffsetY() != null) {
            acceleratedRoiElement
                    .append(addMetadataProperty("OffsetY",
                            Integer.toString(acceleratedRoi.getOffsetY())));
        }

        acceleratedRoiElement
                .append(addMetadataProperty("WidthInBlocks",
                        Integer.toString(acceleratedRoi.getWidthInBlocks())));
        acceleratedRoiElement
                .append(addMetadataProperty("HeightInBlocks",
                        Integer.toString(acceleratedRoi.getHeightInBlocks())));
        acceleratedRoiElement
                .append(addMetadataProperty("AssociatedViewport",
                        Integer.toString(acceleratedRoi.getAssociatedViewport())));
        acceleratedRoiElement
                .append(addMetadataProperty("BoxReference", acceleratedRoi.getBoxReference()));

        acceleratedRoiElement.append(addContentsClosingTag(false));
        acceleratedRoiElement.append(addResourceClosingTag());
        acceleratedRoiElement.append(addContentsClosingTag(true));
        acceleratedRoiElement.append(addResourceClosingTag());

        return acceleratedRoiElement.toString();
    }

}

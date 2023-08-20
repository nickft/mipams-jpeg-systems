package org.mipams.jpeg360.entities.generator;

import org.mipams.jpeg360.entities.Jpeg360ImageMetadata;
import org.mipams.jpeg360.entities.validator.PropertyType;

public class Jpeg360ImageMetadataGenerator extends Jpeg360AbstractGenerator<Jpeg360ImageMetadata> {
    @Override
    public String schemaToRdfXml(Jpeg360ImageMetadata jpeg360ImageMetadata) throws Exception {
        StringBuilder imageMetadata = new StringBuilder(addResourceOpeningTag());

        imageMetadata.append(addName("JPEG360ImageMetadata"));
        imageMetadata.append(addContentsOpeningTag(false));

        imageMetadata.append(addSchemaProperty("JPEG360Version", PropertyType.INTEGER));
        imageMetadata.append(addSchemaProperty("MediaType", PropertyType.STRING));
        imageMetadata.append(addSchemaProperty("ProjectionType", PropertyType.STRING));

        if (jpeg360ImageMetadata.getStereoscopicFormat() != null) {
            imageMetadata.append(addSchemaProperty("StereoscopicFormat", PropertyType.STRING));
        }

        imageMetadata.append(addSchemaProperty("PhiMin", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("PhiMax", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("ThetaMax", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("ThetaMin", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("PhiGravity", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("ThetaGravity", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("CompassPhi", PropertyType.REAL));
        imageMetadata.append(addSchemaProperty("BoxReference", PropertyType.STRING));

        imageMetadata.append(addContentsClosingTag(false));
        imageMetadata.append(addResourceClosingTag());
        return imageMetadata.toString();
    }

    @Override
    public String metadataToRdfXml(Jpeg360ImageMetadata imageMetadata) throws Exception {
        StringBuilder imageMetadataElement = new StringBuilder(addResourceOpeningTag());

        imageMetadataElement.append(addName("JPEG360ImageMetadata"));
        imageMetadataElement.append(addContentsOpeningTag(true));
        imageMetadataElement.append(addResourceOpeningTag());

        imageMetadataElement.append(String.format("<umf:id>%d</umf:id>", imageMetadata.getUmdId()));
        imageMetadataElement.append(addContentsOpeningTag(false));

        imageMetadataElement
                .append(addMetadataProperty("JPEG360Version", Integer.toString(imageMetadata.getVersion())));
        imageMetadataElement.append(addMetadataProperty("MediaType", imageMetadata.getMediaType()));
        imageMetadataElement.append(addMetadataProperty("ProjectionType", imageMetadata.getProjectionType()));
        if (imageMetadata.getStereoscopicFormat() != null) {
            imageMetadataElement
                    .append(addMetadataProperty("StereoscopicFormat", imageMetadata.getStereoscopicFormat()));
        }
        imageMetadataElement.append(addMetadataProperty("PhiMin", Double.toString(imageMetadata.getPhiMin())));
        imageMetadataElement.append(addMetadataProperty("PhiMax", Double.toString(imageMetadata.getPhiMax())));
        imageMetadataElement.append(addMetadataProperty("ThetaMin", Double.toString(imageMetadata.getThetaMin())));
        imageMetadataElement.append(addMetadataProperty("ThetaMax", Double.toString(imageMetadata.getThetaMax())));
        imageMetadataElement.append(addMetadataProperty("PhiGravity", Double.toString(imageMetadata.getPhiGravity())));
        imageMetadataElement
                .append(addMetadataProperty("ThetaGravity", Double.toString(imageMetadata.getThetaGravity())));
        imageMetadataElement.append(addMetadataProperty("CompassPhi", Double.toString(imageMetadata.getCompassPhi())));
        imageMetadataElement.append(addMetadataProperty("BoxReference", imageMetadata.getBoxReference()));

        imageMetadataElement.append(addContentsClosingTag(false));
        imageMetadataElement.append(addResourceClosingTag());
        imageMetadataElement.append(addContentsClosingTag(true));
        imageMetadataElement.append(addResourceClosingTag());

        return imageMetadataElement.toString();
    }
}

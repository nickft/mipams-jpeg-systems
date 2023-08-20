package org.mipams.jpeg360.entities.generator;

import org.mipams.jpeg360.entities.Jpeg360Viewport;
import org.mipams.jpeg360.entities.validator.PropertyType;

public class Jpeg360ViewportGenerator extends Jpeg360AbstractGenerator<Jpeg360Viewport> {

    @Override
    public String schemaToRdfXml(Jpeg360Viewport jpeg360Element) throws Exception {
        StringBuilder viewportElement = new StringBuilder(addResourceOpeningTag());

        viewportElement.append(addName("JPEG360ViewportMetadata"));
        viewportElement.append(addContentsOpeningTag(false));

        viewportElement.append(addSchemaProperty("JPEG360ViewportNumber", PropertyType.INTEGER));
        viewportElement.append(addSchemaProperty("ViewportPhi", PropertyType.REAL));
        viewportElement.append(addSchemaProperty("ViewportTheta", PropertyType.REAL));
        viewportElement.append(addSchemaProperty("ViewportPhiFOV", PropertyType.REAL));
        viewportElement.append(addSchemaProperty("ViewportThetaFOV", PropertyType.REAL));
        viewportElement.append(addSchemaProperty("ViewportRoll", PropertyType.REAL));

        viewportElement.append(addContentsClosingTag(false));
        viewportElement.append(addResourceClosingTag());
        return viewportElement.toString();
    }

    @Override
    public String metadataToRdfXml(Jpeg360Viewport viewport) throws Exception {
        StringBuilder viewportElement = new StringBuilder(addResourceOpeningTag());

        viewportElement.append(addName("JPEG360ViewportMetadata"));
        viewportElement.append(addContentsOpeningTag(true));
        viewportElement.append(addResourceOpeningTag());

        viewportElement.append(String.format("<umf:id>%d</umf:id>", viewport.getUmdId()));
        viewportElement.append(addContentsOpeningTag(false));

        viewportElement
                .append(addMetadataProperty("JPEG360ViewportNumber", Integer.toString(viewport.getNumber())));
        viewportElement.append(addMetadataProperty("ViewportPhi", Double.toString(viewport.getViewportPhi())));
        viewportElement.append(addMetadataProperty("ViewportTheta", Double.toString(viewport.getViewportTheta())));
        viewportElement.append(addMetadataProperty("ViewportPhiFOV", Double.toString(viewport.getViewportPhiFOV())));
        viewportElement
                .append(addMetadataProperty("ViewportThetaFOV", Double.toString(viewport.getViewportThetaFOV())));
        viewportElement.append(addMetadataProperty("ViewportRoll", Double.toString(viewport.getViewportRoll())));

        viewportElement.append(addContentsClosingTag(false));
        viewportElement.append(addResourceClosingTag());
        viewportElement.append(addContentsClosingTag(true));
        viewportElement.append(addResourceClosingTag());

        return viewportElement.toString();
    }

}

package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkViewport;
import org.mipams.jlink.entities.validator.PropertyType;

public class ViewportGenerator extends JlinkElementAbstractGenerator<JlinkViewport> {

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder viewportSchema = new StringBuilder(addResourceOpeningTag());

        viewportSchema.append(addSchema("Viewport"));
        viewportSchema.append(addContentsOpeningTag());

        viewportSchema.append(addSchemaProperty("X", PropertyType.REAL));
        viewportSchema.append(addSchemaProperty("Y", PropertyType.REAL));
        viewportSchema.append(addSchemaProperty("XFOV", PropertyType.REAL));
        viewportSchema.append(addSchemaProperty("YFOV", PropertyType.REAL));
        viewportSchema.append(addSchemaProperty("ID", PropertyType.INTEGER));

        viewportSchema.append(addContentsClosingTag());
        viewportSchema.append(addResourceClosingTag());
        return viewportSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkViewport viewport) throws Exception {
        StringBuilder viewportMetadata = new StringBuilder(addResourceOpeningTag());

        viewportMetadata.append(addSchema("Viewport"));
        viewportMetadata.append("<umf:set><rdf:Bag>");

        viewportMetadata.append(addMetadataProperty("ID", viewport.getId().toString()));
        viewportMetadata.append(addMetadataProperty("X", Double.toString(viewport.getX())));
        viewportMetadata.append(addMetadataProperty("Y", Double.toString(viewport.getY())));
        viewportMetadata.append(addMetadataProperty("XFOV", Double.toString(viewport.getXfov())));
        viewportMetadata.append(addMetadataProperty("YFOV", Double.toString(viewport.getXfov())));

        viewportMetadata.append("</rdf:Bag></umf:set>");
        viewportMetadata.append(addResourceClosingTag());

        return viewportMetadata.toString();
    }

}

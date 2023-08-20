package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkRegion;
import org.mipams.jlink.entities.validator.PropertyType;

public class RegionGenerator extends JlinkElementAbstractGenerator<JlinkRegion> {

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder regionSchema = new StringBuilder(addResourceOpeningTag());

        regionSchema.append(addSchema("Region"));
        regionSchema.append(addContentsOpeningTag());

        regionSchema.append(addSchemaProperty("X", PropertyType.REAL));
        regionSchema.append(addSchemaProperty("Y", PropertyType.REAL));
        regionSchema.append(addSchemaProperty("W", PropertyType.REAL));
        regionSchema.append(addSchemaProperty("H", PropertyType.REAL));
        regionSchema.append(addSchemaProperty("Shape", PropertyType.STRING));
        regionSchema.append(addSchemaProperty("Rotation", PropertyType.REAL));

        regionSchema.append(addContentsClosingTag());
        regionSchema.append(addResourceClosingTag());
        return regionSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkRegion region) throws Exception {
        StringBuilder regionMetadata = new StringBuilder(addResourceOpeningTag());

        regionMetadata.append(addSchema("Region"));
        regionMetadata.append("<umf:set><rdf:Bag>");

        regionMetadata.append(addMetadataProperty("X", Double.toString(region.getX())));
        regionMetadata.append(addMetadataProperty("Y", Double.toString(region.getY())));
        regionMetadata.append(addMetadataProperty("W", Double.toString(region.getW())));
        regionMetadata.append(addMetadataProperty("H", Double.toString(region.getH())));
        regionMetadata.append(addMetadataProperty("Shape", region.getShape()));
        regionMetadata.append(addMetadataProperty("Rotation", Double.toString(region.getRotation())));

        regionMetadata.append("</rdf:Bag></umf:set>");
        regionMetadata.append(addResourceClosingTag());

        return regionMetadata.toString();
    }

}

package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.Region;
import org.mipams.jlink.entities.validator.PropertyType;

public class RegionGenerator extends JlinkElementAbstractGenerator<Region> {

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
    public String metadataToRdfXml(Region region) throws Exception {
        StringBuilder regionMetadata = new StringBuilder(addResourceOpeningTag());

        regionMetadata.append(addSchema("Region"));
        regionMetadata.append("<umf:set><rdf:Bag>");

        if (region.getX() == null) {
            throw new Exception("No X was found for region");
        }
        regionMetadata.append(addMetadataProperty("X", Double.toString(region.getX())));

        if (region.getY() == null) {
            throw new Exception("No Y was found for region");
        }
        regionMetadata.append(addMetadataProperty("Y", Double.toString(region.getY())));

        if (region.getW() == null) {
            throw new Exception("No W was found for region");
        }
        regionMetadata.append(addMetadataProperty("W", Double.toString(region.getW())));

        if (region.getH() == null) {
            throw new Exception("No H was found for region");
        }
        regionMetadata.append(addMetadataProperty("H", Double.toString(region.getH())));

        if (region.getRotation() == null) {
            throw new Exception("No Rotation was found for region");
        }
        regionMetadata.append(addMetadataProperty("Rotation", Double.toString(region.getRotation())));

        regionMetadata.append("</rdf:Bag></umf:set>");
        regionMetadata.append(addResourceClosingTag());

        return regionMetadata.toString();
    }

}

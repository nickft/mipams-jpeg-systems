package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkImage;
import org.mipams.jlink.entities.validator.PropertyType;

public class ImageGenerator extends JlinkElementAbstractGenerator<JlinkImage> {

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder imageSchema = new StringBuilder(addResourceOpeningTag());

        imageSchema.append(addSchema("Image"));
        imageSchema.append(addContentsOpeningTag());

        imageSchema.append(addSchemaProperty("Format", PropertyType.STRING));
        imageSchema.append(addSchemaProperty("Href", PropertyType.STRING));

        imageSchema.append(addContentsClosingTag());
        imageSchema.append(addResourceClosingTag());
        return imageSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkImage image) throws Exception {
        StringBuilder imageMetadata = new StringBuilder(addResourceOpeningTag());

        imageMetadata.append(addSchema("Image"));
        imageMetadata.append("<umf:set><rdf:Bag>");

        imageMetadata.append(addMetadataProperty("Format", image.getFormat()));
        imageMetadata.append(addMetadataProperty("Href", image.getHref()));

        imageMetadata.append("</rdf:Bag></umf:set>");
        imageMetadata.append(addResourceClosingTag());

        return imageMetadata.toString();
    }

}

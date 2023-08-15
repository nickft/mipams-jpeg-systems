package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.Image;
import org.mipams.jlink.entities.validator.PropertyType;

public class ImageGenerator extends JlinkElementAbstractGenerator<Image> {

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
    public String metadataToRdfXml(Image image) throws Exception {
        StringBuilder imageMetadata = new StringBuilder(addResourceOpeningTag());

        imageMetadata.append(addSchema("Image"));
        imageMetadata.append("<umf:set><rdf:Bag>");

        if (image.getFormat() == null) {
            throw new Exception("Format is not specified for image");
        }

        imageMetadata.append(addMetadataProperty("Format", image.getFormat()));

        if (image.getHref() == null) {
            throw new Exception("Href is not specified for image");
        }

        imageMetadata.append(addMetadataProperty("Href", image.getHref()));

        imageMetadata.append("</rdf:Bag></umf:set>");
        imageMetadata.append(addResourceClosingTag());

        return imageMetadata.toString();
    }

}

package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkLink;
import org.mipams.jlink.entities.validator.PropertyType;

public class LinkGenerator extends JlinkElementAbstractGenerator<JlinkLink> {

    RegionGenerator regionGenerator;

    public LinkGenerator() {
        regionGenerator = new RegionGenerator();
    }

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder linkSchema = new StringBuilder(addResourceOpeningTag());

        linkSchema.append(addSchema("Link"));
        linkSchema.append(addContentsOpeningTag());

        linkSchema.append(addSchemaProperty("Duration", PropertyType.INTEGER));
        linkSchema.append(addSchemaProperty("VPID", PropertyType.INTEGER));
        linkSchema.append(addSchemaProperty("Sprite", PropertyType.STRING));
        linkSchema.append(addSchemaProperty("To", PropertyType.STRING));

        linkSchema.append(regionGenerator.schemaToRdfXml());

        linkSchema.append(addContentsClosingTag());
        linkSchema.append(addResourceClosingTag());
        return linkSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkLink link) throws Exception {
        StringBuilder linkMetadata = new StringBuilder(addResourceOpeningTag());

        linkMetadata.append(addSchema("Link"));
        linkMetadata.append("<umf:set><rdf:Bag>");

        linkMetadata.append(addMetadataProperty("Duration", Integer.toString(link.getDuration())));
        linkMetadata.append(addMetadataProperty("VPID", Integer.toString(link.getVpid())));
        linkMetadata.append(addMetadataProperty("To", link.getTo()));
        linkMetadata.append(addMetadataProperty("Sprite", link.getSprite()));
        linkMetadata.append(regionGenerator.metadataToRdfXml(link.getRegion()));

        linkMetadata.append("</rdf:Bag></umf:set>");
        linkMetadata.append(addResourceClosingTag());

        return linkMetadata.toString();
    }

}

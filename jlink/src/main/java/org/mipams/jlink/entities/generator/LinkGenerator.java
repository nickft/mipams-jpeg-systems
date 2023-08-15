package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.Link;
import org.mipams.jlink.entities.validator.PropertyType;

public class LinkGenerator extends JlinkElementAbstractGenerator<Link> {

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
    public String metadataToRdfXml(Link link) throws Exception {
        StringBuilder linkMetadata = new StringBuilder(addResourceOpeningTag());

        linkMetadata.append(addSchema("Link"));
        linkMetadata.append("<umf:set><rdf:Bag>");

        if (link.getDuration() != 600) {
            linkMetadata.append(addMetadataProperty("Duration", Integer.toString(link.getDuration())));
        }

        if (link.getVpid() != 0) {
            linkMetadata.append(addMetadataProperty("VPID", Integer.toString(link.getVpid())));
        }

        if (link.getTo() == null || link.getTo().isBlank()) {
            throw new Exception("No To was specified for link.");
        }
        linkMetadata.append(addMetadataProperty("To", link.getTo()));

        if (link.getSprite() == null || link.getSprite().isBlank()) {
            throw new Exception("No Sprite was specified for link.");
        }
        linkMetadata.append(addMetadataProperty("Sprite", link.getSprite()));

        if (link.getRegion() == null) {
            throw new Exception("No Region is specified for link");
        }

        linkMetadata.append(regionGenerator.metadataToRdfXml(link.getRegion()));

        linkMetadata.append("</rdf:Bag></umf:set>");
        linkMetadata.append(addResourceClosingTag());

        return linkMetadata.toString();
    }

}

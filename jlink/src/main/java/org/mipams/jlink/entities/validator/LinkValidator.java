package org.mipams.jlink.entities.validator;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.mipams.jlink.entities.JlinkLink;

public class LinkValidator extends JlinkAbstractValidator<JlinkLink> {

    RegionValidator regionValidator;

    public LinkValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
        regionValidator = new RegionValidator(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return LinkProperty.getKeysAsList();
    }

    @Override
    protected JlinkLink initializeJlinkElement() {
        return new JlinkLink();
    }

    @Override
    protected boolean supportsSubschemata() {
        return true;
    }

    @Override
    protected void handleSubschema(JlinkLink link, Statement schemaStatement, List<String> allowedPropertyNames,
            boolean isMetadataStructure) throws Exception {
        String schemaName = schemaStatement.getObject().toString();
        List<Statement> schemaContents = getSchemaContents(schemaStatement, isMetadataStructure);

        removePropertyIfExistsOrElseThrowException(schemaName, allowedPropertyNames);

        if (isMetadataStructure) {
            link.setRegion(regionValidator.readFromMetadata(schemaContents));
        } else {
            regionValidator.validateSchema(schemaContents);
        }
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return LinkProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(JlinkLink link, Map<String, String> linkMetadataProperties) throws Exception {
        String linkDuration = linkMetadataProperties.getOrDefault(LinkProperty.DURATION.getKey(), "");
        String linkTo = linkMetadataProperties.getOrDefault(LinkProperty.TO.getKey(), "");
        String linkVpid = linkMetadataProperties.getOrDefault(LinkProperty.VPID.getKey(), "");
        String linkSprite = linkMetadataProperties.getOrDefault(LinkProperty.SPRITE.getKey(), "");

        if (linkDuration.isBlank()) {
            throw new Exception("Duration not specified for link.");
        }
        link.setDuration(Integer.parseInt(linkDuration));

        if (linkTo.isBlank()) {
            throw new Exception("'To' not specified for link.");
        }
        link.setTo(linkTo);

        if (linkVpid.isBlank()) {
            throw new Exception("VPID not specified for link.");
        }
        link.setVpid(Integer.parseInt(linkVpid));

        if (linkSprite.isBlank()) {
            throw new Exception("Sprite not specified for link.");
        }
        link.setSprite(linkSprite);
    }

    enum LinkProperty {
        REGION("Region", PropertyType.SCHEMA),
        DURATION("Duration", PropertyType.INTEGER),
        VPID("VPID", PropertyType.INTEGER),
        SPRITE("Sprite", PropertyType.STRING),
        TO("To", PropertyType.STRING);

        private String key;
        private PropertyType type;

        LinkProperty(String key, PropertyType type) {
            this.key = key;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public PropertyType getType() {
            return type;
        }

        public static List<String> getKeysAsList() {
            List<String> result = new ArrayList<>();
            for (LinkProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static LinkProperty getPropertyFromString(String value) throws Exception {
            for (LinkProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for link schema", value));
        }
    }

}

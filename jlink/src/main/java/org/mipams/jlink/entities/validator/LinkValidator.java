package org.mipams.jlink.entities.validator;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.mipams.jlink.entities.Link;

public class LinkValidator extends JlinkAbstractValidator<Link> {

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
    protected Link initializeJlinkElement() {
        return new Link();
    }

    @Override
    protected boolean supportsSubschemata() {
        return true;
    }

    @Override
    protected void handleSubschema(Link link, Statement schemaStatement, List<String> allowedPropertyNames,
            boolean isMetadataStructure) throws Exception {
        String schemaName = schemaStatement.getObject().stringValue();
        List<Value> schemaContents = getSchemaContents(schemaStatement, isMetadataStructure);

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
    protected void populateObjectFromMap(Link link, Map<String, String> linkMetadataProperties) throws Exception {
        String linkDuration = linkMetadataProperties.getOrDefault(LinkProperty.DURATION.getKey(), "");
        String linkTo = linkMetadataProperties.getOrDefault(LinkProperty.TO.getKey(), "");
        String linkVpid = linkMetadataProperties.getOrDefault(LinkProperty.VPID.getKey(), "");
        String linkSprite = linkMetadataProperties.getOrDefault(LinkProperty.SPRITE.getKey(), "");

        if (!linkDuration.isBlank()) {
            link.setDuration(Integer.parseInt(linkDuration));
        }

        if (linkTo.isBlank()) {
            throw new Exception("No To was specified for link.");
        }
        link.setTo(linkTo);

        if (!linkVpid.isBlank()) {
            link.setVpid(Integer.parseInt(linkVpid));
        }

        if (linkSprite.isBlank()) {
            throw new Exception("No Sprite was specified for link.");
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

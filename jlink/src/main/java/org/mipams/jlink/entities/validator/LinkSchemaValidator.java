package org.mipams.jlink.entities.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.mipams.jlink.entities.Link;
import org.mipams.jlink.entities.Region;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class LinkSchemaValidator {

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

        public static Set<String> getKeysAsList() {
            Set<String> result = new HashSet<>();
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

    enum RegionProperty {
        SHAPE("Shape", PropertyType.STRING),
        X("X", PropertyType.REAL),
        Y("Y", PropertyType.REAL),
        W("W", PropertyType.REAL),
        H("H", PropertyType.REAL),
        ROTATION("Rotation", PropertyType.REAL);

        private String key;
        private PropertyType type;

        RegionProperty(String key, PropertyType type) {
            this.key = key;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public PropertyType getType() {
            return type;
        }

        public static Set<String> getKeysAsList() {
            Set<String> result = new HashSet<>();
            for (RegionProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static RegionProperty getPropertyFromString(String value) throws Exception {
            for (RegionProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for region schema", value));
        }
    }

    Model jlinkModel;
    Map<String, Resource> internalResourceMap;
    Map<String, String> linkMetadataProperties = new HashMap<>();
    Map<String, String> regionMetadataProperties = new HashMap<>();

    public LinkSchemaValidator(Model jlinkModel, Map<String, Resource> internalResourceMap) {
        this.jlinkModel = jlinkModel;
        this.internalResourceMap = new HashMap<>(internalResourceMap);
    }

    public void validateLinkSchema(List<Value> metadataContents) throws Exception {
        validateLinkSchema(metadataContents, true);
    }

    public Link validateLinkMetadata(List<Value> metadataContents) throws Exception {
        validateLinkSchema(metadataContents, false);
        return getLinkFromValidatedInput();
    }

    private void validateLinkSchema(List<Value> schemaDescriptorContents, boolean isSchemaValidation) throws Exception {
        Set<String> occuredProperties = new HashSet<>();

        for (Value i : schemaDescriptorContents) {
            Resource resource = internalResourceMap.get(i.stringValue());

            Optional<Statement> schemaStatement = ValidatorUtils.getSchemaStatement(jlinkModel, resource);

            if (schemaStatement.isEmpty()) {
                String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
                ValidatorUtils.validatePropertyName(propertyName, occuredProperties, LinkProperty.getKeysAsList());
                if (isSchemaValidation) {
                    ValidatorUtils.validatePropertyType(propertyName,
                            LinkProperty.getPropertyFromString(propertyName).getType(),
                            ValidatorUtils.getPropertyType(jlinkModel, resource));
                } else {
                    String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                    linkMetadataProperties.put(propertyName, propertyValue);
                }
            } else {
                String schemaName = schemaStatement.get().getObject().stringValue();
                ValidatorUtils.validatePropertyName(schemaName, occuredProperties, LinkProperty.getKeysAsList());

                if (!LinkProperty.REGION.getKey().equals(schemaName)) {
                    throw new Exception(String.format("Link schema supports does not support %s schema", schemaName));
                }

                validateRegionSchema(schemaStatement.get(), isSchemaValidation);
            }
        }
    }

    private void validateRegionSchema(Statement regionSchemaStatement, boolean isSchemaValidation) throws Exception {
        Statement descriptor = (isSchemaValidation) ? ValidatorUtils.getOptionalValue(jlinkModel,
                regionSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0descriptors")).get()
                : ValidatorUtils.getOptionalValue(jlinkModel,
                        regionSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0set")).get();
        Resource bag = internalResourceMap.get(descriptor.getObject().stringValue());
        List<Value> values = ValidatorUtils.getRdfBagContents(bag, jlinkModel);
        Set<String> occuredProperties = new HashSet<>();
        for (Value i : values) {
            Resource resource = internalResourceMap.get(i.stringValue());
            String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
            ValidatorUtils.validatePropertyName(propertyName, occuredProperties, RegionProperty.getKeysAsList());
            if (isSchemaValidation) {
                ValidatorUtils.validatePropertyType(propertyName,
                        RegionProperty.getPropertyFromString(propertyName).getType(),
                        ValidatorUtils.getPropertyType(jlinkModel, resource));
            } else {
                String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                regionMetadataProperties.put(propertyName, propertyValue);
            }
        }
    }

    private Link getLinkFromValidatedInput() throws Exception {

        Region region = getRegionFromValidatedInput();

        Link link = new Link();
        String linkDuration = linkMetadataProperties.getOrDefault(LinkProperty.DURATION.getKey(), "");
        String linkTo = linkMetadataProperties.getOrDefault(LinkProperty.TO.getKey(), "");
        String linkVpid = linkMetadataProperties.getOrDefault(LinkProperty.VPID.getKey(), "");
        String linkSprite = linkMetadataProperties.getOrDefault(LinkProperty.SPRITE.getKey(), "");

        if (!linkDuration.isBlank()) {
            link.setDuration(Integer.parseInt(linkDuration));
        }

        if (!linkTo.isBlank()) {
            link.setTo(linkTo);
        }

        if (!linkVpid.isBlank()) {
            link.setVpid(Integer.parseInt(linkVpid));
        }

        if (!linkSprite.isBlank()) {
            link.setSprite(linkSprite);
        }

        link.setRegion(region);

        return link;
    }

    private Region getRegionFromValidatedInput() {
        Region region = new Region();

        String regionX = regionMetadataProperties.getOrDefault(RegionProperty.X.getKey(), "");
        String regionY = regionMetadataProperties.getOrDefault(RegionProperty.Y.getKey(), "");
        String regionW = regionMetadataProperties.getOrDefault(RegionProperty.W.getKey(), "");
        String regionShape = regionMetadataProperties.getOrDefault(RegionProperty.SHAPE.getKey(), "");
        String regionRotation = regionMetadataProperties.getOrDefault(RegionProperty.ROTATION.getKey(), "");

        if (!regionX.isBlank()) {
            region.setX(Double.parseDouble(regionX));
        }

        if (!regionY.isBlank()) {
            region.setY(Double.parseDouble(regionY));
        }

        if (!regionW.isBlank()) {
            region.setW(Double.parseDouble(regionW));
        }

        if (!regionShape.isBlank()) {
            region.setShape(regionShape);
        }

        if (!regionRotation.isBlank()) {
            region.setRotation(Double.parseDouble(regionRotation));
        }

        return region;
    }

}

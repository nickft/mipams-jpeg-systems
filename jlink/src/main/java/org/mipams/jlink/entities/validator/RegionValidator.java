package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.mipams.jlink.entities.Region;

public class RegionValidator extends JlinkAbstractValidator<Region> {

    public RegionValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return RegionProperty.getKeysAsList();
    }

    @Override
    protected Region initializeJlinkElement() {
        return new Region();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return RegionProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(Region region, Map<String, String> regionMetadataProperties) throws Exception {
        String regionX = regionMetadataProperties.getOrDefault(RegionProperty.X.getKey(), "");
        String regionY = regionMetadataProperties.getOrDefault(RegionProperty.Y.getKey(), "");
        String regionW = regionMetadataProperties.getOrDefault(RegionProperty.W.getKey(), "");
        String regionH = regionMetadataProperties.getOrDefault(RegionProperty.H.getKey(), "");
        String regionShape = regionMetadataProperties.getOrDefault(RegionProperty.SHAPE.getKey(), "");
        String regionRotation = regionMetadataProperties.getOrDefault(RegionProperty.ROTATION.getKey(), "");

        if (regionX.isBlank()) {
            throw new Exception("No X was specified for region");
        }
        region.setX(Double.parseDouble(regionX));

        if (regionY.isBlank()) {
            throw new Exception("No Y was specified for region");
        }
        region.setY(Double.parseDouble(regionY));

        if (regionW.isBlank()) {
            throw new Exception("No W was specified for region");
        }
        region.setW(Double.parseDouble(regionW));

        if (regionH.isBlank()) {
            throw new Exception("No H was specified for region");
        }
        region.setH(Double.parseDouble(regionH));

        if (!regionShape.isBlank()) {
            region.setShape(regionShape);
        }

        if (regionRotation.isBlank()) {
            throw new Exception("No Rotation was specified for region");
        }
        region.setRotation(Double.parseDouble(regionRotation));
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

        public static List<String> getKeysAsList() {
            List<String> result = new ArrayList<>();
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

}

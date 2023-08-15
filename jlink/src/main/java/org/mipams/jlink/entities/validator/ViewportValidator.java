package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.mipams.jlink.entities.Viewport;

public class ViewportValidator extends JlinkAbstractValidator<Viewport> {

    public ViewportValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return ViewportProperty.getKeysAsList();
    }

    @Override
    protected Viewport initializeJlinkElement() {
        return new Viewport();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return ViewportProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(Viewport viewport, Map<String, String> viewportMetadataProperties)
            throws Exception {
        String viewportX = viewportMetadataProperties.getOrDefault(ViewportProperty.X.getKey(), "");
        String viewportY = viewportMetadataProperties.getOrDefault(ViewportProperty.Y.getKey(), "");
        String viewportXFov = viewportMetadataProperties.getOrDefault(ViewportProperty.XFOV.getKey(), "");
        String viewportYFov = viewportMetadataProperties.getOrDefault(ViewportProperty.YFOV.getKey(), "");
        String viewportId = viewportMetadataProperties.getOrDefault(ViewportProperty.ID.getKey(), "");

        if (viewportId.isBlank()) {
            throw new Exception("No ID was specified for viewport.");
        } else {
            int val = Integer.parseInt(viewportId);
            viewport.setId(val);
        }

        if (!viewportX.isBlank()) {
            double val = Double.parseDouble(viewportX);
            viewport.setX(val);
        }

        if (!viewportY.isBlank()) {
            double val = Double.parseDouble(viewportY);
            viewport.setY(val);
        }

        if (!viewportXFov.isBlank()) {
            double val = Double.parseDouble(viewportXFov);
            viewport.setXfov(val);
        }

        if (!viewportYFov.isBlank()) {
            double val = Double.parseDouble(viewportYFov);
            viewport.setYfov(val);
        }
    }

    enum ViewportProperty {
        X("X", PropertyType.REAL),
        Y("Y", PropertyType.REAL),
        XFOV("XFOV", PropertyType.REAL),
        YFOV("YFOV", PropertyType.REAL),
        ID("ID", PropertyType.INTEGER);

        private String key;
        private PropertyType type;

        ViewportProperty(String key, PropertyType type) {
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
            for (ViewportProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static ViewportProperty getPropertyFromString(String value) throws Exception {
            for (ViewportProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for viewport schema", value));
        }
    }
}

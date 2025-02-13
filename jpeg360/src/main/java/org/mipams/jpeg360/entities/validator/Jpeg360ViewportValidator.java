package org.mipams.jpeg360.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.mipams.jpeg360.entities.Jpeg360Viewport;

public class Jpeg360ViewportValidator extends Jpeg360AbstractValidator<Jpeg360Viewport> {

    public Jpeg360ViewportValidator(Model jpeg360Model, Map<String, Resource> subjectNameToResourceMap) {
        super(jpeg360Model, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return Jpeg360ViewportProperty.getKeysAsList();
    }

    @Override
    protected Jpeg360Viewport initializeJpeg360Element() {
        return new Jpeg360Viewport();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return Jpeg360ViewportProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(Jpeg360Viewport viewport,
            Map<String, String> viewportProperties) throws Exception {

        String jpeg360ViewportNumber = viewportProperties
                .getOrDefault(Jpeg360ViewportProperty.VERSION.getKey(), "");
        String viewportPhi = viewportProperties.getOrDefault(Jpeg360ViewportProperty.PHI.getKey(), "");
        String viewportTheta = viewportProperties.getOrDefault(Jpeg360ViewportProperty.THETA.getKey(), "");
        String viewportPhiFOV = viewportProperties.getOrDefault(Jpeg360ViewportProperty.PHI_FOV.getKey(), "");
        String viewportThetaFOV = viewportProperties.getOrDefault(Jpeg360ViewportProperty.THETA_FOV.getKey(),
                "");
        String viewportRoll = viewportProperties.getOrDefault(Jpeg360ViewportProperty.ROLL.getKey(), "");

        if (jpeg360ViewportNumber.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.VERSION.getKey()));
        }
        viewport.setNumber(Integer.parseInt(jpeg360ViewportNumber));

        if (viewportPhi.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.PHI.getKey()));
        }
        viewport.setViewportPhi(Double.parseDouble(viewportPhi));

        if (viewportTheta.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.THETA.getKey()));
        }
        viewport.setViewportTheta(Double.parseDouble(viewportTheta));

        if (viewportPhiFOV.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.PHI_FOV.getKey()));
        }
        viewport.setViewportPhiFOV(Double.parseDouble(viewportPhiFOV));

        if (viewportThetaFOV.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.THETA_FOV.getKey()));
        }
        viewport.setViewportThetaFOV(Double.parseDouble(viewportThetaFOV));

        if (viewportRoll.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ViewportProperty.ROLL.getKey()));
        }
        viewport.setViewportRoll(Double.parseDouble(viewportRoll));
    }

    @Override
    protected void validateElementBasedOnSchemaProperties(Jpeg360Viewport element,
            List<String> propertiesYetNotFoundInStructure) throws Exception {
        super.validateElementBasedOnSchemaProperties(element, propertiesYetNotFoundInStructure);

        validateMetadataValues(element);
    }

    private void validateMetadataValues(Jpeg360Viewport element) throws Exception {

        if (element.getViewportPhi() > 360.0 || element.getViewportPhi() < -360.0) {
            throw new Exception(
                    String.format("ViewportPhi out of bounds. Expected Range [-360,360], found %f",
                            element.getViewportPhi()));
        }

        if (element.getViewportPhiFOV() > 720 || element.getViewportPhiFOV() < 0) {
            throw new Exception(
                    String.format("ViewportPhiFOV out of bounds. Expected Range [0,720], found %f",
                            element.getViewportPhiFOV()));
        }

        if (element.getViewportTheta() > 180.0 || element.getViewportTheta() < -180.0) {
            throw new Exception(
                    String.format("ViewportTheta out of bounds. Expected Range [-180,180], found %f",
                            element.getViewportTheta()));
        }

        if (element.getViewportThetaFOV() > 360 || element.getViewportThetaFOV() < 0) {
            throw new Exception(
                    String.format("ViewportThetaFOV out of bounds. Expected Range [0,360], found %f",
                            element.getViewportThetaFOV()));
        }

        if (element.getViewportRoll() < 0 || element.getViewportRoll() > 360) {
            String.format("ViewportRoll out of bounds. Expected Range [0,360), found %f",
                    element.getViewportRoll());
        }

    }

    enum Jpeg360ViewportProperty {
        VERSION("JPEG360ViewportNumber", PropertyType.INTEGER),
        PHI("ViewportPhi", PropertyType.REAL),
        THETA("ViewportTheta", PropertyType.REAL),
        PHI_FOV("ViewportPhiFOV", PropertyType.REAL),
        THETA_FOV("ViewportThetaFOV", PropertyType.REAL),
        ROLL("ViewportRoll", PropertyType.REAL);

        private String key;
        private PropertyType type;

        Jpeg360ViewportProperty(String key, PropertyType type) {
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
            for (Jpeg360ViewportProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static Jpeg360ViewportProperty getPropertyFromString(String value) throws Exception {
            for (Jpeg360ViewportProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(
                    String.format("Property %s is not supported for Jpeg 360 Viewport schema", value));
        }
    }
}

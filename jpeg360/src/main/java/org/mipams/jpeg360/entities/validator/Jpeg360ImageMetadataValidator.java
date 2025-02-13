package org.mipams.jpeg360.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.mipams.jpeg360.entities.Jpeg360ImageMetadata;

public class Jpeg360ImageMetadataValidator extends Jpeg360AbstractValidator<Jpeg360ImageMetadata> {
    public Jpeg360ImageMetadataValidator(Model jpeg360Model, Map<String, Resource> subjectNameToResourceMap) {
        super(jpeg360Model, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return Jpeg360ImageMetadataProperty.getKeysAsList();
    }

    @Override
    protected Jpeg360ImageMetadata initializeJpeg360Element() {
        return new Jpeg360ImageMetadata();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return Jpeg360ImageMetadataProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(Jpeg360ImageMetadata imageMetadata,
            Map<String, String> imageMetadataProperties) throws Exception {

        String jpeg360Version = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.VERSION.getKey(), "");
        if (!(jpeg360Version.equals("1") || jpeg360Version.equals("2"))) {
            throw new Exception(String.format("Invalid JPEG 360 Version %s", jpeg360Version));
        }

        String mediaType = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.MEDIA_TYPE.getKey(), "");
        if (mediaType.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.MEDIA_TYPE.getKey()));
        }
        imageMetadata.setMediaType(mediaType);

        String projectionType = imageMetadataProperties
                .getOrDefault(Jpeg360ImageMetadataProperty.PROTECTION_TYPE.getKey(), "");
        if (projectionType.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.PROTECTION_TYPE.getKey()));
        }
        imageMetadata.setProjectionType(projectionType);

        String stereoscopicFormat = imageMetadataProperties
                .getOrDefault(Jpeg360ImageMetadataProperty.STEREOSCOPIC_FORMAT.getKey(), "");
        if (!stereoscopicFormat.isBlank()) {
            if (jpeg360Version.equals("1")) {
                throw new Exception("Stereoscopic format is not supported in version 1 of JPEG 360 schema");
            } else {
                if (!List.of("side-by-side", "top-bottom", "extended").contains(stereoscopicFormat)) {
                    throw new Exception(String.format("Invalid stereoscopic format value: %s", stereoscopicFormat));
                }

                if (!imageMetadata.getProjectionType().equals("Equirectangular")) {
                    throw new Exception(String.format(
                            "For stereoscopic images projection type shall be set to Equirectangular. Found %s",
                            imageMetadata.getProjectionType()));
                }

                imageMetadata.setStereoscopicFormat(stereoscopicFormat);
            }
        }

        String phiMin = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.PHI_MIN.getKey(), "");
        if (phiMin.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.PHI_MIN.getKey()));
        }
        imageMetadata.setPhiMin(Double.parseDouble(phiMin));

        String phiMax = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.PHI_MAX.getKey(), "");
        if (phiMax.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.PHI_MAX.getKey()));
        }
        imageMetadata.setPhiMax(Double.parseDouble(phiMax));

        String thetaMin = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.THETA_MIN.getKey(), "");

        if (thetaMin.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.THETA_MIN.getKey()));
        }
        imageMetadata.setThetaMin(Double.parseDouble(thetaMin));

        String thetaMax = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.THETA_MAX.getKey(), "");
        if (thetaMax.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.THETA_MAX.getKey()));
        }
        imageMetadata.setThetaMax(Double.parseDouble(thetaMax));

        String phiGravity = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.PHI_GRAVITY.getKey(), "");
        if (phiGravity.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.PHI_GRAVITY.getKey()));
        }
        imageMetadata.setPhiGravity(Double.parseDouble(phiGravity));

        String thetaGravity = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.THETA_GRAVITY.getKey(),
                "");
        if (thetaGravity.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.THETA_GRAVITY.getKey()));
        }
        imageMetadata.setThetaGravity(Double.parseDouble(thetaGravity));

        String compassPhi = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.COMPASS_PHI.getKey(), "");
        if (compassPhi.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.COMPASS_PHI.getKey()));
        }
        imageMetadata.setCompassPhi(Double.parseDouble(compassPhi));

        String boxReference = imageMetadataProperties.getOrDefault(Jpeg360ImageMetadataProperty.BOX_REFERENCE.getKey(),
                "");
        if (boxReference.isBlank()) {
            throw new Exception(
                    String.format("%s shall be provided.", Jpeg360ImageMetadataProperty.BOX_REFERENCE.getKey()));
        }

        if (!stereoscopicFormat.isBlank() && !stereoscopicFormat.equals("extended")) {
            if (!boxReference.equals("conventional")) {
                throw new Exception(
                        "'Conventional' shall be provided when StereoscopicFormat is set to 'side-by-side' and 'top-bottom'.");
            }
        }
        imageMetadata.setBoxReference(boxReference);
    }

    @Override
    protected void validateElementBasedOnSchemaProperties(Jpeg360ImageMetadata element,
            List<String> propertiesYetNotFoundInStructure) throws Exception {

        if (propertiesYetNotFoundInStructure.size() == 0) {
            if (element.getStereoscopicFormat() == null) {
                throw new Exception(
                        "JPEG 360 Metadata Schema descriptor for Stereoscopic Format exists but no such tag is found in metadata element with umf id: %d");
            }
            return;
        }

        if (propertiesYetNotFoundInStructure.get(0).equals(Jpeg360ImageMetadataProperty.STEREOSCOPIC_FORMAT.getKey())) {
            if (element.getStereoscopicFormat() != null) {
                throw new Exception(
                        "JPEG 360 Metadata Schema descriptor for Stereoscopic Format does not exist but no a tag is found");
            }
            removePropertyIfExistsOrElseThrowException(Jpeg360ImageMetadataProperty.STEREOSCOPIC_FORMAT.getKey(),
                    propertiesYetNotFoundInStructure);
        }

        super.validateElementBasedOnSchemaProperties(element, propertiesYetNotFoundInStructure);

        validateMetadataValues(element);
    }

    private void validateMetadataValues(Jpeg360ImageMetadata element) throws Exception {

        if (element.getPhiMax() > 360.0 || element.getPhiMax() < -360.0) {
            throw new Exception(
                    String.format("PhiMax out of bounds. Expected Range [-360,360], found %f", element.getPhiMax()));
        }

        if (element.getPhiMin() > 360.0 || element.getPhiMin() < -360.0) {
            throw new Exception(
                    String.format("PhiMin out of bounds. Expected Range [-360,360], found %f", element.getPhiMin()));
        }

        Double phiDifference = element.getPhiMax() - element.getPhiMin();
        if (phiDifference > 360.0) {
            throw new Exception(String.format("(PhiMax - PhiMin) out of bounds. Expected difference <=360, found %f",
                    phiDifference));
        }

        if (element.getThetaMax() > 180.0 || element.getThetaMax() < -180.0) {
            throw new Exception(
                    String.format("ThetaMax out of bounds. Expected Range [-180,180], found %f",
                            element.getThetaMax()));
        }

        if (element.getThetaMin() > 180.0 || element.getThetaMin() < -180.0) {
            throw new Exception(
                    String.format("ThetaMin out of bounds. Expected Range [-180,180], found %f",
                            element.getThetaMin()));
        }

        Double thetaDifference = element.getThetaMax() - element.getThetaMin();
        if (thetaDifference > 180.0) {
            throw new Exception(
                    String.format("(getThetaMax - getThetaMin) out of bounds. Expected difference <=180, found %f",
                            thetaDifference));
        }

        if ((element.getPhiGravity() >= 180.0 + element.getPhiMax())
                || (element.getPhiGravity() <= -180.0 + element.getPhiMin())) {
            throw new Exception(String.format("PhiGravity out of bounds. Expected Range [%f,%f], found %f",
                    -180.0 + element.getPhiMin(), 180.0 + element.getPhiMax(), element.getPhiGravity()));
        }

        if ((element.getThetaGravity() >= 90.0 + +element.getThetaMax())
                || (element.getPhiGravity() <= -90.0 + element.getThetaMin())) {
            throw new Exception(String.format("ThetaGravity out of bounds. Expected Range [%f,%f], found %f",
                    -90.0 + element.getThetaMin(), 90.0 + element.getThetaMax(), element.getThetaGravity()));
        }

        if (element.getCompassPhi() < 0 || element.getCompassPhi() >= 360) {
            String.format("CompassPhi out of bounds. Expected Range [0,360), found %f",
                    element.getCompassPhi());
        }

        if (element.getCompassPhi() < element.getPhiMin() || element.getCompassPhi() > element.getPhiMax()) {
            String.format("CompassPhi out of Phi bounds. Expected Range [%f,%f], found %f",
                    element.getPhiMin(), element.getPhiMax(), element.getCompassPhi());
        }
    }

    enum Jpeg360ImageMetadataProperty {
        VERSION("JPEG360Version", PropertyType.INTEGER),
        MEDIA_TYPE("MediaType", PropertyType.STRING),
        PROTECTION_TYPE("ProjectionType", PropertyType.STRING),
        STEREOSCOPIC_FORMAT("StereoscopicFormat", PropertyType.STRING),
        PHI_MIN("PhiMin", PropertyType.REAL),
        PHI_MAX("PhiMax", PropertyType.REAL),
        THETA_MAX("ThetaMax", PropertyType.REAL),
        THETA_MIN("ThetaMin", PropertyType.REAL),
        PHI_GRAVITY("PhiGravity", PropertyType.REAL),
        THETA_GRAVITY("ThetaGravity", PropertyType.REAL),
        COMPASS_PHI("CompassPhi", PropertyType.REAL),
        BOX_REFERENCE("BoxReference", PropertyType.STRING);

        private String key;
        private PropertyType type;

        Jpeg360ImageMetadataProperty(String key, PropertyType type) {
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
            for (Jpeg360ImageMetadataProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static Jpeg360ImageMetadataProperty getPropertyFromString(String value) throws Exception {
            for (Jpeg360ImageMetadataProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(
                    String.format("Property %s is not supported for Jpeg 360 Image Metadata schema", value));
        }
    }
}

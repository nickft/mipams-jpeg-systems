package org.mipams.jpeg360.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.mipams.jpeg360.entities.Jpeg360AcceleratedRoi;
import org.mipams.jpeg360.entities.Jpeg360ImageMetadata;
import org.mipams.jpeg360.entities.Jpeg360Metadata;
import org.mipams.jpeg360.entities.Jpeg360Viewport;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class Jpeg360MetadataValidator extends Jpeg360AbstractValidator<Jpeg360Metadata> {

    Jpeg360ImageMetadataValidator imageMetadataValidator;
    Jpeg360ViewportValidator imageViewportValidator;
    Jpeg360AcceleratedRoiValidator acceleratedRoisValidator;

    public Jpeg360MetadataValidator(Model jpeg360Model, Map<String, Resource> subjectNameToResourceMap) {
        super(jpeg360Model, subjectNameToResourceMap);
        imageMetadataValidator = new Jpeg360ImageMetadataValidator(jpeg360Model, subjectNameToResourceMap);
        imageViewportValidator = new Jpeg360ViewportValidator(jpeg360Model, subjectNameToResourceMap);
        acceleratedRoisValidator = new Jpeg360AcceleratedRoiValidator(jpeg360Model, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return Jpeg360MetadataProperty.getKeysAsList();
    }

    @Override
    protected Jpeg360Metadata initializeJpeg360Element() {
        return new Jpeg360Metadata();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        throw new UnsupportedOperationException(
                String.format("JPEG 360 schema does not support property %s at the highest level", propertyName));
    }

    @Override
    protected void populateObjectFromMap(Jpeg360Metadata jpeg360MetadataElement, Map<String, String> metadataProperties)
            throws Exception {
    }

    @Override
    public void validateMetadataBasedOnSchema(Jpeg360Metadata element, List<Value> contents) throws Exception {
        List<String> propertiesYetNotFoundInStructure = getAllowedProperties();

        for (Value i : contents) {
            Resource resource = getSubjectNameToResourceMap().get(i.stringValue());

            String schemaName = getSchemaNameFromResourceOrElseThrowException(resource);
            Jpeg360MetadataProperty schema = Jpeg360MetadataProperty.getSchemaPropertyFromString(schemaName);

            List<Value> schemaContents = getSchemaContentsFromResourceOrElseThrowException(resource);

            removePropertyIfExistsOrElseThrowException(schemaName, propertiesYetNotFoundInStructure);
            if (Jpeg360MetadataProperty.IMAGE_METADATA.equals(schema)) {
                imageMetadataValidator.validateMetadataBasedOnSchema(element.getImageMetadata(),
                        schemaContents);
            } else if (Jpeg360MetadataProperty.VIEWPORT.equals(schema)) {
                for (Jpeg360Viewport viewport : element.getViewports()) {
                    imageViewportValidator.validateMetadataBasedOnSchema(viewport, schemaContents);
                }
            } else if (Jpeg360MetadataProperty.ACCELERATED_ROI.equals(schema)) {
                if (element.getAcceleratedROIs().isEmpty()) {
                    throw new Exception("Accelerated ROIs schema is defined, but no metadata elements were found");
                }

                for (Jpeg360AcceleratedRoi acceleratedRoi : element.getAcceleratedROIs()) {
                    acceleratedRoisValidator.validateMetadataBasedOnSchema(acceleratedRoi, schemaContents);
                }
            } else {
                throw new Exception(String.format("Metadata Element %s is not expected in Jpeg360Metadata Structure"));
            }
        }
    }

    private String getSchemaNameFromResourceOrElseThrowException(Resource resource) throws Exception {
        Optional<Statement> nameStatement = ValidatorUtils.getOptionalValue(jpeg360Model, resource,
                iri("http://ns.intel.com/umf/2.0name"));
        if (nameStatement.isEmpty()) {
            throw new Exception("Name is not found for schema");
        }
        return ValidatorUtils.getPropertyName(jpeg360Model, nameStatement.get().getSubject());
    }

    private List<Value> getSchemaContentsFromResourceOrElseThrowException(Resource resource) throws Exception {
        Optional<Statement> schemaContents = ValidatorUtils.getOptionalValue(jpeg360Model, resource,
                iri("http://ns.intel.com/umf/2.0fields"));
        if (schemaContents.isEmpty()) {
            throw new Exception("Schema contentents <umf:fields> element is not found for schema");
        }
        resource = getSubjectNameToResourceMap().get(schemaContents.get().getObject().stringValue());

        return ValidatorUtils.getRdfBagContents(resource, jpeg360Model);
    }

    @Override
    public Jpeg360Metadata readFromMetadata(List<Value> contents) throws Exception {
        Jpeg360Metadata jpeg360MetadataElement = initializeJpeg360Element();
        for (Value i : contents) {
            Resource resource = getSubjectNameToResourceMap().get(i.stringValue());

            String schemaName = getSchemaNameFromResourceOrElseThrowException(resource);
            Jpeg360MetadataProperty schema = Jpeg360MetadataProperty.getSchemaPropertyFromString(schemaName);

            Resource metadataParentElement = getMetadataParentElement(resource);
            Integer umfId = getUmfIdFromResourceOrElseThrowException(metadataParentElement);

            try {
                List<Value> metadataContents = getSchemaContentsFromResourceOrElseThrowException(
                        metadataParentElement);

                if (Jpeg360MetadataProperty.IMAGE_METADATA.equals(schema)) {
                    if (jpeg360MetadataElement.getImageMetadata() != null) {
                        throw new Exception(
                                "A JPEG 360 XML schema cannot have more than one image metadata structure defined");
                    }

                    Jpeg360ImageMetadata metadata = imageMetadataValidator.readFromMetadata(metadataContents);
                    metadata.setUmfId(umfId);
                    jpeg360MetadataElement.setImageMetadata(metadata);
                } else if (Jpeg360MetadataProperty.VIEWPORT.equals(schema)) {
                    Jpeg360Viewport viewport = imageViewportValidator.readFromMetadata(metadataContents);
                    viewport.setUmfId(umfId);
                    jpeg360MetadataElement.addViewport(viewport);
                } else if (Jpeg360MetadataProperty.ACCELERATED_ROI.equals(schema)) {
                    Jpeg360AcceleratedRoi acceleratedRoi = acceleratedRoisValidator
                            .readFromMetadata(metadataContents);
                    acceleratedRoi.setUmfId(umfId);
                    jpeg360MetadataElement.addAcceleratedRois(acceleratedRoi);
                } else {
                    throw new Exception(
                            String.format("Metadata Element %s is not expected in Jpeg360Metadata Structure", schema));
                }
            } catch (Exception e) {
                throw new Exception(
                        String.format("Error in metadata element with umf id: %d. Reason: %s", umfId, e.getMessage()),
                        e);
            }
        }

        return jpeg360MetadataElement;
    }

    private Integer getUmfIdFromResourceOrElseThrowException(Resource resource) throws Exception {
        Optional<Statement> metadataIdStatement = ValidatorUtils.getOptionalValue(jpeg360Model, resource,
                iri("http://ns.intel.com/umf/2.0id"));
        if (metadataIdStatement.isEmpty()) {
            throw new Exception("Umf Id is not found for metadata element");
        }
        return Integer.parseInt(metadataIdStatement.get().getObject().stringValue());
    }

    private Resource getMetadataParentElement(Resource resource) {
        Optional<Statement> metadataSetStatement = ValidatorUtils.getOptionalValue(jpeg360Model, resource,
                iri("http://ns.intel.com/umf/2.0set"));
        resource = getSubjectNameToResourceMap().get(metadataSetStatement.get().getObject().stringValue());

        List<Value> internalSchemaContents = ValidatorUtils.getRdfBagContents(resource, jpeg360Model);
        return getSubjectNameToResourceMap().get(internalSchemaContents.get(0).stringValue());
    }

    enum Jpeg360MetadataProperty {
        IMAGE_METADATA("JPEG360ImageMetadata", PropertyType.SCHEMA),
        VIEWPORT("JPEG360ViewportMetadata", PropertyType.SCHEMA),
        ACCELERATED_ROI("JPEG360AcceleratedROI", PropertyType.SCHEMA);

        private String key;
        private PropertyType type;

        Jpeg360MetadataProperty(String key, PropertyType type) {
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
            for (Jpeg360MetadataProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static Jpeg360MetadataProperty getPropertyFromString(String value) throws Exception {
            for (Jpeg360MetadataProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for jpeg360 schema", value));
        }

        public static Jpeg360MetadataProperty getSchemaPropertyFromString(String value) throws Exception {
            Jpeg360MetadataProperty schemaProperty = getPropertyFromString(value);

            if (schemaProperty.equals(IMAGE_METADATA) || schemaProperty.equals(VIEWPORT)
                    || schemaProperty.equals(ACCELERATED_ROI)) {
                return schemaProperty;
            }

            throw new Exception(String.format("Invalid schema %s for jpeg360 schema", value));
        }
    }

}

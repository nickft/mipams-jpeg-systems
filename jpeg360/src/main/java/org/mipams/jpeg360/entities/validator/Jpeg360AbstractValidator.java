package org.mipams.jpeg360.entities.validator;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public abstract class Jpeg360AbstractValidator<T> implements ElementValidator<T> {

    protected Model jpeg360Model;
    protected Map<String, Resource> subjectNameToResourceMap;

    public Jpeg360AbstractValidator(Model jpeg360Model, Map<String, Resource> subjectNameToResourceMap) {
        this.jpeg360Model = jpeg360Model;
        this.subjectNameToResourceMap = new HashMap<>(subjectNameToResourceMap);
    }

    protected Model getModel() {
        return jpeg360Model;
    }

    protected Map<String, Resource> getSubjectNameToResourceMap() {
        return subjectNameToResourceMap;
    }

    @Override
    public T readFromMetadata(List<Statement> contents) throws Exception {
        List<String> allowedPropertyKeys = getAllowedProperties();
        Map<String, String> metadataProperties = new HashMap<>();

        T jpeg360Element = initializeJpeg360Element();

        for (Statement i : contents) {

            Resource resource = getSubjectNameToResourceMap().get(i.getObject().toString());

            String propertyName = ValidatorUtils.getPropertyName(jpeg360Model, resource);
            removePropertyIfExistsOrElseThrowException(propertyName, allowedPropertyKeys);

            String propertyValue = ValidatorUtils.getPropertyValue(jpeg360Model, resource);
            metadataProperties.put(propertyName, propertyValue);
        }
        populateObjectFromMap(jpeg360Element, metadataProperties);

        return jpeg360Element;
    }

    @Override
    public void validateMetadataBasedOnSchema(T element, List<Statement> contents) throws Exception {
        List<String> propertiesYetNotFoundInStructure = getAllowedProperties();

        for (Statement i : contents) {
            Resource resource = getSubjectNameToResourceMap().get(i.getObject().toString());

            String propertyName = ValidatorUtils.getPropertyName(jpeg360Model, resource);
            removePropertyIfExistsOrElseThrowException(propertyName, propertiesYetNotFoundInStructure);

            ValidatorUtils.validatePropertyType(propertyName,
                    getExpectedTypeFromPropertyName(propertyName),
                    ValidatorUtils.getPropertyType(jpeg360Model, resource));
        }

        validateElementBasedOnSchemaProperties(element, propertiesYetNotFoundInStructure);
    }

    protected void validateElementBasedOnSchemaProperties(T element, List<String> propertiesYetNotFoundInStructure)
            throws Exception {
        if (propertiesYetNotFoundInStructure.size() != 0) {
            throw new Exception(String.format("During schema validation the following properties are not found %s",
                    propertiesYetNotFoundInStructure.toString()));
        }
    }

    protected abstract List<String> getAllowedProperties();

    protected abstract T initializeJpeg360Element();

    protected abstract PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception;

    protected void handleSubschema(T object, Statement schemaStatement, List<String> allowedPropertyNames,
            boolean isMetadataStructure) throws Exception {
    }

    protected abstract void populateObjectFromMap(T object, Map<String, String> metadataProperties) throws Exception;

    protected void removePropertyIfExistsOrElseThrowException(String propertyName, List<String> propertyList)
            throws Exception {

        boolean exists = propertyList.removeIf(p -> p.equals(propertyName));

        if (!exists) {
            throw new Exception(String.format("Property or schema %s has already been specified or it is not supported",
                    propertyName));
        }
    }

    protected List<Statement> getSchemaContents(Statement schemaStatement, boolean isMetadataStructure)
            throws Exception {
        Statement descriptor = getChildStatementBasedOnElementType(schemaStatement, isMetadataStructure);
        Resource bag = getSubjectNameToResourceMap().get(descriptor.getObject().toString());

        return ValidatorUtils.getRdfBagContents(bag, jpeg360Model);
    }

    protected Statement getChildStatementBasedOnElementType(Statement parentStatement, boolean isSchemaElement) {
        return (!isSchemaElement) ? ValidatorUtils.getOptionalValue(jpeg360Model,
                parentStatement.getSubject(), ResourceFactory.createProperty("http://ns.intel.com/umf/2.0descriptors"))
                .get()
                : ValidatorUtils.getOptionalValue(jpeg360Model,
                        parentStatement.getSubject(), ResourceFactory.createProperty("http://ns.intel.com/umf/2.0set"))
                        .get();
    }
}

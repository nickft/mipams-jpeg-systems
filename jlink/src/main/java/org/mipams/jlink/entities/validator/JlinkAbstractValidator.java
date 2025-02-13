package org.mipams.jlink.entities.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import java.util.List;


public abstract class JlinkAbstractValidator<T> implements ElementValidator<T> {

    protected Model jlinkModel;
    protected Map<String, Resource> subjectNameToResourceMap;

    public JlinkAbstractValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        this.jlinkModel = jlinkModel;
        this.subjectNameToResourceMap = new HashMap<>(subjectNameToResourceMap);
    }

    protected Model getModel() {
        return jlinkModel;
    }

    protected Map<String, Resource> getSubjectNameToResourceMap() {
        return subjectNameToResourceMap;
    }

    @Override
    public T readFromMetadata(List<Statement> contents) throws Exception {
        return validateElement(contents, true);
    }

    @Override
    public void validateSchema(List<Statement> contents) throws Exception {
        validateElement(contents, false);
    }

    private T validateElement(List<Statement> sceneContents, boolean isMetadataStructure) throws Exception {
        Map<String, String> metadataProperties = new HashMap<>();
        List<String> allowedPropertyKeys = getAllowedProperties();

        T jlinkElement = initializeJlinkElement();

        for (Statement i : sceneContents) {
            Resource resource = getSubjectNameToResourceMap().get(i.getObject().toString());

            if (supportsSubschemata() && ValidatorUtils.isSchemaStatement(jlinkModel, resource)) {
                Optional<Statement> schemaStatement = ValidatorUtils.getSchemaStatement(jlinkModel, resource);
                handleSubschema(jlinkElement, schemaStatement.get(), allowedPropertyKeys, isMetadataStructure);
            } else {
                String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
                removePropertyIfExistsOrElseThrowException(propertyName, allowedPropertyKeys);

                if (!isMetadataStructure) {
                    ValidatorUtils.validatePropertyType(propertyName,
                            getExpectedTypeFromPropertyName(propertyName),
                            ValidatorUtils.getPropertyType(jlinkModel, resource));
                } else {
                    String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                    metadataProperties.put(propertyName, propertyValue);
                }
            }
        }

        if (isMetadataStructure) {
            populateObjectFromMap(jlinkElement, metadataProperties);
        } else {
            if (allowedPropertyKeys.size() != 0) {
                throw new Exception(String.format("During schema validation the following properties are not found %s",
                        allowedPropertyKeys.toString()));
            }
        }

        return (isMetadataStructure) ? jlinkElement : null;
    }

    protected abstract List<String> getAllowedProperties();

    protected abstract T initializeJlinkElement();

    protected boolean supportsSubschemata() {
        return false;
    }

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

    protected List<Statement> getSchemaContents(Statement schemaStatement, boolean isMetadataStructure) throws Exception {
        Statement descriptor = getChildStatementBasedOnElementType(schemaStatement, isMetadataStructure);
        Resource bag = getSubjectNameToResourceMap().get(descriptor.getObject().toString());

        return ValidatorUtils.getRdfBagContents(bag, jlinkModel);
    }

    protected Statement getChildStatementBasedOnElementType(Statement parentStatement, boolean isSchemaElement) {
        return (!isSchemaElement) ? ValidatorUtils.getOptionalValue(jlinkModel,
                parentStatement.getSubject(), ResourceFactory.createProperty("http://ns.intel.com/umf/2.0descriptors")).get()
                : ValidatorUtils.getOptionalValue(jlinkModel,
                        parentStatement.getSubject(), ResourceFactory.createProperty("http://ns.intel.com/umf/2.0set")).get();
    }
}

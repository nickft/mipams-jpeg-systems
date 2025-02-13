package org.mipams.jlink.entities.validator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.mipams.jlink.entities.JlinkElement;

public class JlinkValidator extends JlinkAbstractValidator<JlinkElement> {

    SceneValidator sceneValidator;
    LinkValidator linkValidator;

    public JlinkValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
        sceneValidator = new SceneValidator(jlinkModel, subjectNameToResourceMap);
        linkValidator = new LinkValidator(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return JlinkProperty.getKeysAsList();
    }

    @Override
    protected JlinkElement initializeJlinkElement() {
        return new JlinkElement();
    }

    @Override
    protected boolean supportsSubschemata() {
        return true;
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        throw new UnsupportedOperationException(
                String.format("JLINK schema does not support property %s at the highest level", propertyName));
    }

    @Override
    protected void populateObjectFromMap(JlinkElement jlinkElement, Map<String, String> metadataProperties)
            throws Exception {
    }

    @Override
    protected void handleSubschema(JlinkElement jlinkElement, Statement schemaStatement,
            List<String> allowedPropertyNames,
            boolean isMetadataStructure) throws Exception {
        String schemaName = schemaStatement.getObject().toString();

        List<Statement> schemaContents = getSchemaContents(schemaStatement, isMetadataStructure);
        JlinkProperty schema = JlinkProperty.getSchemaPropertyFromString(schemaName);

        if (isMetadataStructure) {
            if (JlinkProperty.LINK.equals(schema)) {
                jlinkElement.addLink(linkValidator.readFromMetadata(schemaContents));
            } else {
                if (jlinkElement.getScene() != null) {
                    throw new Exception("A JLINK XML structure box cannot have more than one scenes defined");
                }
                jlinkElement.setScene(sceneValidator.readFromMetadata(schemaContents));
            }
        } else {
            if (JlinkProperty.LINK.equals(schema)) {
                removePropertyIfExistsOrElseThrowException(schemaName, allowedPropertyNames);
                linkValidator.validateSchema(schemaContents);
            } else {
                removePropertyIfExistsOrElseThrowException(schemaName, allowedPropertyNames);
                sceneValidator.validateSchema(schemaContents);
            }
        }
    }

    enum JlinkProperty {
        LINK("Link", PropertyType.SCHEMA),
        SCENE("Scene", PropertyType.SCHEMA);

        private String key;
        private PropertyType type;

        JlinkProperty(String key, PropertyType type) {
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
            for (JlinkProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static JlinkProperty getPropertyFromString(String value) throws Exception {
            for (JlinkProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for jlink schema", value));
        }

        public static JlinkProperty getSchemaPropertyFromString(String value) throws Exception {
            JlinkProperty schemaProperty = getPropertyFromString(value);

            if (schemaProperty.equals(LINK) || schemaProperty.equals(SCENE)) {
                return schemaProperty;
            }

            throw new Exception(String.format("Invalid schema %s for jlink schema", value));
        }
    }
}

package org.mipams.jlink.entities.validator;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.mipams.jlink.entities.Scene;

public class SceneValidator extends JlinkAbstractValidator<Scene> {

    ImageValidator imageValidator;
    ViewportValidator viewportValidator;

    public SceneValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
        imageValidator = new ImageValidator(jlinkModel, subjectNameToResourceMap);
        viewportValidator = new ViewportValidator(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return SceneProperty.getKeysAsList();
    }

    @Override
    protected Scene initializeJlinkElement() {
        return new Scene();
    }

    @Override
    protected boolean supportsSubschemata() {
        return true;
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return SceneProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void handleSubschema(Scene scene, Statement schemaStatement, List<String> allowedPropertyNames,
            boolean isMetadataStructure) throws Exception {
        String schemaName = schemaStatement.getObject().stringValue();

        List<Value> schemaContents = getSchemaContents(schemaStatement, isMetadataStructure);
        SceneProperty schema = SceneProperty.getSchemaPropertyFromString(schemaName);

        if (SceneProperty.IMAGE.equals(schema)) {
            removePropertyIfExistsOrElseThrowException(schemaName, allowedPropertyNames);
        }

        if (isMetadataStructure) {
            if (SceneProperty.VIEWPORT.equals(schema)) {
                scene.addViewport(viewportValidator.readFromMetadata(schemaContents));
            } else {
                if (scene.getImage() != null) {
                    throw new Exception("A scene cannot have more than one image codestreams assigned");
                }
                scene.setImage(imageValidator.readFromMetadata(schemaContents));
            }
        } else {
            if (SceneProperty.VIEWPORT.equals(schema)) {
                removePropertyIfExistsOrElseThrowException(schemaName, allowedPropertyNames);
                viewportValidator.validateSchema(schemaContents);
            } else {
                imageValidator.validateSchema(schemaContents);
            }
        }
    }

    @Override
    protected void populateObjectFromMap(Scene scene, Map<String, String> sceneMetadataProperties) throws Exception {
        String sceneTitle = sceneMetadataProperties.getOrDefault(SceneProperty.TITLE.getKey(), "");
        String sceneVersion = sceneMetadataProperties.getOrDefault(SceneProperty.VERSION.getKey(), "");
        String sceneNote = sceneMetadataProperties.getOrDefault(SceneProperty.NOTE.getKey(), "");

        if (!sceneTitle.isBlank()) {
            scene.setTitle(sceneTitle);
        }

        if (!sceneVersion.isBlank()) {
            scene.setVersion(sceneVersion);
        }

        if (!sceneNote.isBlank()) {
            scene.setNote(sceneNote);
        }
    }

    enum SceneProperty {
        VERSION("Version", PropertyType.STRING),
        TITLE("Title", PropertyType.STRING),
        NOTE("Note", PropertyType.STRING),
        VIEWPORT("Viewport", PropertyType.SCHEMA),
        IMAGE("Image", PropertyType.SCHEMA);

        private String key;
        private PropertyType type;

        SceneProperty(String key, PropertyType type) {
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
            for (SceneProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static SceneProperty getPropertyFromString(String value) throws Exception {
            for (SceneProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for scene schema", value));
        }

        public static SceneProperty getSchemaPropertyFromString(String value) throws Exception {
            SceneProperty schemaProperty = getPropertyFromString(value);

            if (schemaProperty.equals(VIEWPORT) || schemaProperty.equals(IMAGE)) {
                return schemaProperty;
            }

            throw new Exception(String.format("Invalid schema %s for scene schema", value));
        }
    }

}

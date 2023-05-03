package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
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
import org.mipams.jlink.entities.Image;
import org.mipams.jlink.entities.Scene;
import org.mipams.jlink.entities.Viewport;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class SceneSchemaValidator {
    enum SceneProperty {
        VERSION("Version", PropertyType.INTEGER),
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

        public static Set<String> getKeysAsList() {
            Set<String> result = new HashSet<>();
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

        public static Set<String> getKeysAsList() {
            Set<String> result = new HashSet<>();
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

    enum ImageProperty {
        FORMAT("Format", PropertyType.STRING),
        HREF("Href", PropertyType.STRING);

        private String key;
        private PropertyType type;

        ImageProperty(String key, PropertyType type) {
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
            for (ImageProperty property : values()) {
                result.add(property.getKey());
            }
            return result;
        }

        public static ImageProperty getPropertyFromString(String value) throws Exception {
            for (ImageProperty property : values()) {
                if (property.getKey().equals(value)) {
                    return property;
                }
            }

            throw new Exception(String.format("Property %s is not supported for image schema", value));
        }
    }

    Model jlinkModel;
    Map<String, Resource> internalResourceMap;

    Map<String, String> sceneMetadataProperties = new HashMap<>();
    Map<String, String> viewportMetadataProperties = new HashMap<>();
    Map<String, String> imageMetadataProperties = new HashMap<>();

    public SceneSchemaValidator(Model jlinkModel, Map<String, Resource> internalResourceMap) {
        this.jlinkModel = jlinkModel;
        this.internalResourceMap = new HashMap<>(internalResourceMap);
    }

    public void validateSceneSchema(List<Value> schemaDescriptorContents) throws Exception {
        validateSceneSchema(schemaDescriptorContents, true);
    }

    public Scene validateSceneMetadata(ArrayList<Value> metadataContents) throws Exception {
        validateSceneSchema(metadataContents, false);
        return getSceneFromValidatedInput();
    }

    private void validateSceneSchema(List<Value> schemaDescriptorContents, boolean isSchemaValidation)
            throws Exception {
        Set<String> occuredProperties = new HashSet<>();

        for (Value i : schemaDescriptorContents) {
            Resource resource = internalResourceMap.get(i.stringValue());

            Optional<Statement> schemaStatement = ValidatorUtils.getSchemaStatement(jlinkModel, resource);

            if (schemaStatement.isEmpty()) {
                String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
                ValidatorUtils.validatePropertyName(propertyName, occuredProperties, SceneProperty.getKeysAsList());

                if (isSchemaValidation) {
                    ValidatorUtils.validatePropertyType(propertyName,
                            SceneProperty.getPropertyFromString(propertyName).getType(),
                            ValidatorUtils.getPropertyType(jlinkModel, resource));
                } else {
                    String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                    sceneMetadataProperties.put(propertyName, propertyValue);
                }
            } else {
                String schemaName = schemaStatement.get().getObject().stringValue();
                ValidatorUtils.validatePropertyName(schemaName, occuredProperties, SceneProperty.getKeysAsList());
                SceneProperty schema = SceneProperty.getSchemaPropertyFromString(schemaName);

                if (SceneProperty.VIEWPORT.equals(schema)) {
                    validateViewportSchema(schemaStatement.get(), isSchemaValidation);
                } else {
                    validateImageSchema(schemaStatement.get(), isSchemaValidation);
                }
            }
        }
    }

    private void validateViewportSchema(Statement viewportSchemaStatement, boolean isSchemaValidation)
            throws Exception {
        Statement descriptor = (isSchemaValidation) ? ValidatorUtils.getOptionalValue(jlinkModel,
                viewportSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0descriptors")).get()
                : ValidatorUtils.getOptionalValue(jlinkModel,
                        viewportSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0set")).get();
        Resource bag = internalResourceMap.get(descriptor.getObject().stringValue());
        List<Value> values = ValidatorUtils.getRdfBagContents(bag, jlinkModel);
        Set<String> occuredProperties = new HashSet<>();
        for (Value i : values) {
            Resource resource = internalResourceMap.get(i.stringValue());
            String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
            ValidatorUtils.validatePropertyName(propertyName, occuredProperties, ViewportProperty.getKeysAsList());
            if (isSchemaValidation) {
                ValidatorUtils.validatePropertyType(propertyName,
                        ViewportProperty.getPropertyFromString(propertyName).getType(),
                        ValidatorUtils.getPropertyType(jlinkModel, resource));
            } else {
                String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                viewportMetadataProperties.put(propertyName, propertyValue);
            }
        }
    }

    private void validateImageSchema(Statement imageSchemaStatement, boolean isSchemaValidation) throws Exception {
        Statement descriptor = (isSchemaValidation) ? ValidatorUtils.getOptionalValue(jlinkModel,
                imageSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0descriptors")).get()
                : ValidatorUtils.getOptionalValue(jlinkModel,
                        imageSchemaStatement.getSubject(), iri("http://ns.intel.com/umf/2.0set")).get();
        Resource bag = internalResourceMap.get(descriptor.getObject().stringValue());
        List<Value> values = ValidatorUtils.getRdfBagContents(bag, jlinkModel);
        Set<String> occuredProperties = new HashSet<>();
        for (Value i : values) {
            Resource resource = internalResourceMap.get(i.stringValue());
            String propertyName = ValidatorUtils.getPropertyName(jlinkModel, resource);
            ValidatorUtils.validatePropertyName(propertyName, occuredProperties, ImageProperty.getKeysAsList());
            if (isSchemaValidation) {
                ValidatorUtils.validatePropertyType(propertyName,
                        ImageProperty.getPropertyFromString(propertyName).getType(),
                        ValidatorUtils.getPropertyType(jlinkModel, resource));
            } else {
                String propertyValue = ValidatorUtils.getPropertyValue(jlinkModel, resource);
                imageMetadataProperties.put(propertyName, propertyValue);
            }
        }
    }

    private Scene getSceneFromValidatedInput() throws Exception {

        Viewport viewport = getViewportFromValidatedInput();
        Image image = getImageFromValidatedInput();

        Scene scene = new Scene();
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

        scene.setImage(image);
        scene.setViewport(viewport);

        return scene;
    }

    private Viewport getViewportFromValidatedInput() throws Exception {
        Viewport viewport = new Viewport();
        String viewportX = viewportMetadataProperties.getOrDefault(ViewportProperty.X.getKey(), "");
        String viewportY = viewportMetadataProperties.getOrDefault(ViewportProperty.Y.getKey(), "");
        String viewportXFov = viewportMetadataProperties.getOrDefault(ViewportProperty.XFOV.getKey(), "");
        String viewportYFov = viewportMetadataProperties.getOrDefault(ViewportProperty.YFOV.getKey(), "");
        String viewportId = viewportMetadataProperties.getOrDefault(ViewportProperty.ID.getKey(), "");

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

        if (!viewportId.isBlank()) {
            int val = Integer.parseInt(viewportId);
            viewport.setId(val);
        }

        return viewport;

    }

    private Image getImageFromValidatedInput() throws Exception {
        Image image = new Image();
        String format = imageMetadataProperties.getOrDefault(ImageProperty.FORMAT.getKey(), "");
        String href = imageMetadataProperties.getOrDefault(ImageProperty.HREF.getKey(), "");

        if (!format.isBlank()) {
            image.setFormat(format);
        }

        if (!href.isBlank()) {
            image.setHref(href);
        }

        return image;
    }
}

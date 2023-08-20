package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.mipams.jlink.entities.JlinkImage;

public class ImageValidator extends JlinkAbstractValidator<JlinkImage> {

    public ImageValidator(Model jlinkModel, Map<String, Resource> subjectNameToResourceMap) {
        super(jlinkModel, subjectNameToResourceMap);
    }

    @Override
    protected List<String> getAllowedProperties() {
        return ImageProperty.getKeysAsList();
    }

    @Override
    protected JlinkImage initializeJlinkElement() {
        return new JlinkImage();
    }

    @Override
    protected PropertyType getExpectedTypeFromPropertyName(String propertyName) throws Exception {
        return ImageProperty.getPropertyFromString(propertyName).getType();
    }

    @Override
    protected void populateObjectFromMap(JlinkImage image, Map<String, String> imageMetadataProperties)
            throws Exception {
        String format = imageMetadataProperties.getOrDefault(ImageProperty.FORMAT.getKey(), "");
        String href = imageMetadataProperties.getOrDefault(ImageProperty.HREF.getKey(), "");

        if (format.isBlank()) {
            throw new Exception("No format was specified for image");
        }
        image.setFormat(format);

        if (href.isBlank()) {
            throw new Exception("No href was specified for image");
        }
        image.setHref(href);
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

        public static List<String> getKeysAsList() {
            List<String> result = new ArrayList<>();
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

}

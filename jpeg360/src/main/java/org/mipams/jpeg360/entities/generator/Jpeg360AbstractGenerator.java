package org.mipams.jpeg360.entities.generator;

import org.mipams.jpeg360.entities.validator.PropertyType;

public abstract class Jpeg360AbstractGenerator<T> implements Jpeg360ElementGenerator<T> {
    protected String addResourceOpeningTag() {
        return "<rdf:li rdf:parseType=\"Resource\">";
    }

    protected String addResourceClosingTag() {
        return "</rdf:li>";
    }

    protected String addContentsOpeningTag(boolean isMetadataElement) {
        return (isMetadataElement) ? "<umf:set><rdf:Bag>" : "<umf:fields><rdf:Bag>";
    }

    protected String addContentsClosingTag(boolean isMetadataElement) {
        return (isMetadataElement) ? "</rdf:Bag></umf:set>" : "</rdf:Bag></umf:fields>";
    }

    protected String addSchema(String schemaName) {
        return String.format("<umf:schema>%s</umf:schema>", schemaName);
    }

    protected String addName(String name) {
        return String.format("<umf:name>%s</umf:name>", name);
    }

    protected String addSchemaProperty(String propertyKey, PropertyType propertyType) {
        StringBuilder propertyResource = new StringBuilder(addResourceOpeningTag());
        propertyResource.append(String.format("<umf:name>%s</umf:name>", propertyKey));
        propertyResource.append(String.format("<umf:type>%s</umf:type>", propertyType.name().toLowerCase()));
        propertyResource.append(addResourceClosingTag());
        return propertyResource.toString();
    }

    protected String addMetadataProperty(String propertyKey, String propertyValue) {
        StringBuilder propertyResource = new StringBuilder(addResourceOpeningTag());
        propertyResource.append(String.format("<umf:name>%s</umf:name>", propertyKey));
        propertyResource.append(String.format("<rdf:value>%s</rdf:value>", propertyValue));
        propertyResource.append(addResourceClosingTag());
        return propertyResource.toString();
    }
}

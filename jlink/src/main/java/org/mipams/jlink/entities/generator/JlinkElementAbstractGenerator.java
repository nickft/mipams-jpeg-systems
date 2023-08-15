package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.validator.PropertyType;

public abstract class JlinkElementAbstractGenerator<T> implements JlinkElementGenerator<T> {

    protected String addResourceOpeningTag() {
        return "<rdf:li rdf:parseType=\"Resource\">";
    }

    protected String addResourceClosingTag() {
        return "</rdf:li>";
    }

    protected String addContentsOpeningTag() {
        return "<umf:descriptors><rdf:Bag>";
    }

    protected String addContentsClosingTag() {
        return "</rdf:Bag></umf:descriptors>";
    }

    protected String addSchema(String schemaName) {
        return String.format("<umf:schema>%s</umf:schema>", schemaName);
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

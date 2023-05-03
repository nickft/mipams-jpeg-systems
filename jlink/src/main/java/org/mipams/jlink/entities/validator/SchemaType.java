package org.mipams.jlink.entities.validator;

public enum SchemaType {
    SCENE("Scene"),
    LINK("Link");

    protected String description;

    SchemaType(String dsc) {
        this.description = dsc;
    }

    public static SchemaType getSchemaType(String type) throws Exception {
        if (type.equalsIgnoreCase(SCENE.description)) {
            return SCENE;
        } else if (type.equalsIgnoreCase(LINK.description)) {
            return LINK;
        } else {
            throw new Exception(String.format("Schema type %s not defined", type));
        }
    }
}

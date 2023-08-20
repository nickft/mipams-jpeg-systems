package org.mipams.jpeg360.entities.generator;

public interface Jpeg360ElementGenerator<T> {
    public String schemaToRdfXml(T jpeg360Element) throws Exception;

    public String metadataToRdfXml(T jpeg360Element) throws Exception;
}

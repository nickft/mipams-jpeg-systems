package org.mipams.jlink.entities.generator;

public interface JlinkElementGenerator<T> {
    public String schemaToRdfXml() throws Exception;

    public String metadataToRdfXml(T jlinkElement) throws Exception;
}

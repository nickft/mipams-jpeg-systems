package org.mipams.jpeg360.entities.validator;

import java.util.List;

import org.apache.jena.rdf.model.Statement;

public interface ElementValidator<T> {
    public T readFromMetadata(List<Statement> descriptorContents) throws Exception;

    public void validateMetadataBasedOnSchema(T element, List<Statement> contents) throws Exception;
}
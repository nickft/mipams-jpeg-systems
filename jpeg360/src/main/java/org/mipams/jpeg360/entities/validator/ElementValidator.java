package org.mipams.jpeg360.entities.validator;

import java.util.List;

import org.eclipse.rdf4j.model.Value;

public interface ElementValidator<T> {
    public T readFromMetadata(List<Value> descriptorContents) throws Exception;

    public void validateMetadataBasedOnSchema(T element, List<Value> contents) throws Exception;
}
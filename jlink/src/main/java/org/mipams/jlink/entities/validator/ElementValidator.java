package org.mipams.jlink.entities.validator;

import java.util.List;

import org.eclipse.rdf4j.model.Value;

public interface ElementValidator<T> {
    public T readFromMetadata(List<Value> descriptorContents) throws Exception;

    public void validateSchema(List<Value> descriptorContents) throws Exception;
}

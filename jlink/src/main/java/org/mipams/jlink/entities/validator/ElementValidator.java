package org.mipams.jlink.entities.validator;

import java.util.List;

import org.apache.jena.rdf.model.Statement;

public interface ElementValidator<T> {
    public T readFromMetadata(List<Statement> descriptorContents) throws Exception;

    public void validateSchema(List<Statement> descriptorContents) throws Exception;
}

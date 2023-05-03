package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class SchemaValidator {

    private static final Logger logger = Logger.getLogger(SchemaValidator.class.getName());

    Model jlinkModel;
    Map<String, Resource> internalResourceMap;

    public SchemaValidator(Model jlinkModel, Map<String, Resource> internalResourceMap) {
        this.jlinkModel = jlinkModel;
        this.internalResourceMap = new HashMap<>(internalResourceMap);
    }

    public void validateSchemata(Collection<Statement> statements) throws Exception {

        if (statements.size() != 2) {
            throw new Exception("Expected exactly 2 schema definitions");
        }

        for (Statement st : statements) {
            validateSchema(st);
        }
    }

    private void validateSchema(Statement statement) throws Exception {
        SchemaType schemaType = SchemaType.getSchemaType(statement.getObject().stringValue());

        Statement schemaDefinitions = getSchemaDescriptors(statement);

        Resource bag = internalResourceMap.get(schemaDefinitions.getObject().stringValue());
        ArrayList<Value> schemaDescriptorContents = ValidatorUtils.getRdfBagContents(bag, jlinkModel);

        if (SchemaType.SCENE.equals(schemaType)) {
            SceneSchemaValidator validator = new SceneSchemaValidator(jlinkModel, internalResourceMap);
            validator.validateSceneSchema(schemaDescriptorContents);
        } else {
            LinkSchemaValidator validator = new LinkSchemaValidator(jlinkModel, internalResourceMap);
            validator.validateLinkSchema(schemaDescriptorContents);
        }
    }

    private Statement getSchemaDescriptors(Statement statement) throws Exception {
        Resource subject = internalResourceMap.get(statement.getSubject().stringValue());

        Optional<Statement> schemaDescriptors = ValidatorUtils.getOptionalValue(jlinkModel, subject,
                iri("http://ns.intel.com/umf/2.0descriptors"));

        if (schemaDescriptors.isEmpty()) {
            throw new Exception("Expected type for schema definition");
        }

        return schemaDescriptors.get();
    }

}

package org.mipams.jlink.entities.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.mipams.jlink.entities.Link;
import org.mipams.jlink.entities.Scene;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class MetadataValidator {

    private Model jlinkModel;
    private Map<String, Resource> internalResourceMap;
    private List<Scene> scenes = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    public MetadataValidator(Model jlinkModel, Map<String, Resource> internalResourceMap) {
        this.jlinkModel = jlinkModel;
        this.internalResourceMap = new HashMap<>(internalResourceMap);
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void validateMetadata(Collection<Statement> statements) throws Exception {
        for (Statement st : statements) {
            validateMetadataSchema(st);
        }
    }

    private void validateMetadataSchema(Statement statement) throws Exception {
        SchemaType schemaType = SchemaType.getSchemaType(statement.getObject().stringValue());

        Statement metadataSetStatement = getMetadataSet(statement);

        Resource bag = internalResourceMap.get(metadataSetStatement.getObject().stringValue());
        ArrayList<Value> metadataContents = ValidatorUtils.getRdfBagContents(bag, jlinkModel);

        if (SchemaType.SCENE.equals(schemaType)) {
            SceneSchemaValidator validator = new SceneSchemaValidator(jlinkModel, internalResourceMap);
            scenes.add(validator.validateSceneMetadata(metadataContents));
        } else {
            LinkSchemaValidator validator = new LinkSchemaValidator(jlinkModel, internalResourceMap);
            links.add(validator.validateLinkMetadata(metadataContents));
        }
    }

    private Statement getMetadataSet(Statement statement) throws Exception {
        Resource subject = internalResourceMap.get(statement.getSubject().stringValue());

        Optional<Statement> metadataSetStatement = ValidatorUtils.getOptionalValue(jlinkModel, subject,
                iri("http://ns.intel.com/umf/2.0set"));

        if (metadataSetStatement.isEmpty()) {
            throw new Exception("Expected type for metadata definition");
        }

        return metadataSetStatement.get();
    }
}

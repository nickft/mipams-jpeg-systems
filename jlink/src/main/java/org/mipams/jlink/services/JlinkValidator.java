package org.mipams.jlink.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.mipams.jlink.entities.validator.MetadataValidator;
import org.mipams.jlink.entities.validator.SchemaValidator;
import org.mipams.jlink.entities.validator.ValidatorUtils;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.XmlBox;
import org.springframework.stereotype.Service;

import static org.eclipse.rdf4j.model.util.Values.iri;

@Service
public class JlinkValidator {

    private static final Logger logger = Logger.getLogger(JlinkValidator.class.getName());

    public void validateSchema(JumbfBox xmlContentTypeJumbfBox) {
        XmlBox xmpBox = (XmlBox) xmlContentTypeJumbfBox.getContentBoxList().get(0);

        logger.info("Validating JLINK Schema");
        try {
            validateXmlBoxContents(xmpBox);
        } catch (Exception e) {
            logger.severe(String.format("JLINK Schema validation has failed: %s", e.getMessage()));
        }
    }

    private void validateXmlBoxContents(XmlBox xmpBox) throws Exception {

        byte[] rdfSchemaInBytes = stripXmpTag(xmpBox.getContent());
        ValueFactory factory = SimpleValueFactory.getInstance();
        try (InputStream is = new ByteArrayInputStream(rdfSchemaInBytes)) {
            Model jlinkModel = null;
            try {
                jlinkModel = Rio.parse(is, "adobe:ns:meta", RDFFormat.RDFXML);

                Iterator<Statement> nextId = jlinkModel
                        .getStatements(null, iri(factory, "http://ns.intel.com/umf/2.0next-id"), null).iterator();

                if (!nextId.hasNext()) {
                    throw new Exception("JLINK next id was not found");
                }
                logger.log(Level.FINE, String.format("id: %s", nextId.next().getObject().stringValue()));

                Map<String, Resource> internalResourceMap = initializeResourceMap(jlinkModel);
                validateSchemasContent(jlinkModel, internalResourceMap);

                validateMetadataContent(jlinkModel, internalResourceMap);
            } catch (RDFParseException e) {
                e.printStackTrace();
            } catch (UnsupportedRDFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Resource> initializeResourceMap(Model jlinkModel) {
        Map<String, Resource> internalResourceMap = new HashMap<>();
        for (Statement st : jlinkModel) {
            internalResourceMap.put(st.getSubject().stringValue(), st.getSubject());
        }
        return internalResourceMap;
    }

    private byte[] stripXmpTag(byte[] content) throws IOException {
        try (
                InputStream is = new ByteArrayInputStream(content);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                ByteArrayOutputStream bOutput = new ByteArrayOutputStream();) {

            String line;
            boolean xmpLineEncountered = false;
            while ((line = br.readLine()) != null) {

                // TODO validate xmpPacket contents
                if (!line.trim().startsWith("<") && xmpLineEncountered) {
                    xmpLineEncountered = false;
                    continue;
                }

                if (line.trim().startsWith("<x:xmpmeta") || line.trim().startsWith("</x:xmpmeta")) {
                    xmpLineEncountered = true;
                    continue;
                } else {
                    xmpLineEncountered = false;
                }

                bOutput.write((line + "\n").getBytes());
            }

            return bOutput.toByteArray();
        }
    }

    private void validateSchemasContent(Model jlinkModel, Map<String, Resource> internalResourceMap) throws Exception {
        Optional<Statement> schemaStatement = ValidatorUtils.getOptionalValue(jlinkModel, iri("adobe:ns:meta"),
                iri("http://ns.intel.com/umf/2.0schemas"));

        if (schemaStatement.isEmpty()) {
            throw new Exception("Schema doesn't exist");
        }

        Resource schemasContentsResource = internalResourceMap.get(schemaStatement.get().getObject().stringValue());
        List<Value> schemaDescriptorResourceNames = ValidatorUtils.getRdfBagContents(schemasContentsResource,
                jlinkModel);

        List<Resource> schemaDescriptorContents = schemaDescriptorResourceNames.stream()
                .map(v -> internalResourceMap.get(v.stringValue())).collect(Collectors.toList());

        List<Statement> statements = schemaDescriptorContents.stream()
                .map(r -> ValidatorUtils.getOptionalValue(jlinkModel, r, null).get()).collect(Collectors.toList());

        SchemaValidator schemaValidator = new SchemaValidator(jlinkModel, internalResourceMap);
        schemaValidator.validateSchemata(statements);
    }

    private void validateMetadataContent(Model jlinkModel, Map<String, Resource> internalResourceMap) throws Exception {
        Optional<Statement> metadataStatement = ValidatorUtils.getOptionalValue(jlinkModel, iri("adobe:ns:meta"),
                iri("http://ns.intel.com/umf/2.0metadata"));

        Resource metadataContentsResource = internalResourceMap.get(metadataStatement.get().getObject().stringValue());
        List<Value> metadataDescriptorResourceNames = ValidatorUtils.getRdfBagContents(metadataContentsResource,
                jlinkModel);

        List<Resource> metadataContentContents = metadataDescriptorResourceNames.stream()
                .map(v -> internalResourceMap.get(v.stringValue())).collect(Collectors.toList());

        List<Statement> statements = metadataContentContents.stream()
                .map(r -> ValidatorUtils.getOptionalValue(jlinkModel, r, null).get()).collect(Collectors.toList());

        MetadataValidator metadataValidator = new MetadataValidator(jlinkModel, internalResourceMap);
        metadataValidator.validateMetadata(statements);

        logger.info(String.format("Recorded scenes: %s", String.join(",",
                metadataValidator.getScenes().stream().map(s -> s.toString()).collect(Collectors.toList()))));
        logger.info(String.format("Recorded links: %s", String.join(",",
                metadataValidator.getLinks().stream().map(s -> s.toString()).collect(Collectors.toList()))));
    }
}

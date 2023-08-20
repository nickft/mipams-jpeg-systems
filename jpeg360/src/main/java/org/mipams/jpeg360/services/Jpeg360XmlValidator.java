package org.mipams.jpeg360.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jpeg360.entities.Jpeg360ImageMetadata;
import org.mipams.jpeg360.entities.Jpeg360Metadata;
import org.mipams.jpeg360.entities.Jpeg360Viewport;
import org.mipams.jpeg360.entities.validator.Jpeg360MetadataValidator;
import org.mipams.jpeg360.entities.validator.ValidatorUtils;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class Jpeg360XmlValidator {
    private static final Logger logger = Logger.getLogger(Jpeg360XmlValidator.class.getName());

    public Jpeg360Metadata validateSchema(JumbfBox xmlContentTypeJumbfBox) throws MipamsException {
        XmlBox xmpBox = (XmlBox) xmlContentTypeJumbfBox.getContentBoxList().get(0);

        try {
            return validateXmlBoxContents(xmpBox);
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("JPEG 360 Schema validation has failed: %s", e.getMessage()), e);
            throw new MipamsException(e);
        }
    }

    private Jpeg360Metadata validateXmlBoxContents(XmlBox xmpBox) throws Exception {

        byte[] rdfSchemaInBytes = stripXmpTags(xmpBox.getContent());
        ValueFactory factory = SimpleValueFactory.getInstance();
        try (InputStream is = new ByteArrayInputStream(rdfSchemaInBytes)) {
            Model jpeg360Model = Rio.parse(is, "adobe:ns:meta", RDFFormat.RDFXML);

            Iterator<Statement> nextIdStatement = jpeg360Model
                    .getStatements(null, iri(factory, "http://ns.intel.com/umf/2.0next-id"), null).iterator();

            if (!nextIdStatement.hasNext()) {
                throw new Exception("JPEG 360 next id was not found");
            }

            Integer nextId = Integer.parseInt(nextIdStatement.next().getObject().stringValue());

            Map<String, Resource> internalResourceMap = initializeResourceMap(jpeg360Model);

            Jpeg360Metadata element = readMetadataContent(jpeg360Model, internalResourceMap);
            element.setNextId(nextId);

            validateMetadataBasedOnSchema(element, jpeg360Model, internalResourceMap);

            return element;
        }
    }

    private Map<String, Resource> initializeResourceMap(Model jpeg360Model) {
        Map<String, Resource> internalResourceMap = new HashMap<>();
        for (Statement st : jpeg360Model) {
            internalResourceMap.put(st.getSubject().stringValue(), st.getSubject());
        }
        return internalResourceMap;
    }

    private byte[] stripXmpTags(byte[] content) throws Exception {
        String xmlContent = new String(content, StandardCharsets.UTF_8);

        if (startsWithXmpXPacketTag(xmlContent)) {
            xmlContent = stripXmpOptionalHeadersXpacket(xmlContent);

            if (startsWithXmpMetadataTag(xmlContent)) {
                xmlContent = stripXmpOptionalHeadersXmpMetadata(xmlContent);
            }
        } else if (startsWithXmpMetadataTag(xmlContent)) {
            xmlContent = stripXmpOptionalHeadersXmpMetadata(xmlContent);

            if (startsWithXmpXPacketTag(xmlContent)) {
                xmlContent = stripXmpOptionalHeadersXpacket(xmlContent);
            }
        }

        return xmlContent.getBytes(StandardCharsets.UTF_8);
    }

    private boolean startsWithXmpXPacketTag(String xmlContent) {
        return xmlContent.startsWith("<?xpacket");
    }

    private String stripXmpOptionalHeadersXpacket(String xmlContent) throws Exception {
        checkValidityOfXPacketHeader(xmlContent);

        Pattern pattern = Pattern.compile(
                "<\\?xpacket.*\\?>(.*)<\\?xpacket end=\"[wr]\"\\?>",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            throw new Exception("JPEG 360 Corrupted");
        }
    }

    private void checkValidityOfXPacketHeader(String xmlContent) throws Exception {
        xmlContent = xmlContent.replace("'", "\"");

        Pattern pattern = Pattern.compile(
                "<\\?xpacket begin=\"\ufeff?\" id=\"[a-zA-Z0-9]*\"\\?>(.*)<\\?xpacket end=\"[wr]\"\\?>",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);

        if (!matcher.find()) {
            throw new Exception("Malformed XPacket Header");
        }
    }

    private boolean startsWithXmpMetadataTag(String xmlContent) {
        return xmlContent.startsWith("<x:xmpmeta");
    }

    private String stripXmpOptionalHeadersXmpMetadata(String xmlContent) throws Exception {
        checkValidityOfXmpMetadata(xmlContent);

        Pattern pattern = Pattern.compile(
                "<x:xmpmeta\\s*xmlns:x=.adobe:ns:meta\\/.(\\s\\S*=.[^\"\']*.)*>(.*)</x:xmpmeta>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);

        if (matcher.find()) {
            return matcher.group(2).trim();
        } else {
            throw new Exception("JPEG 360 Corrupted");
        }
    }

    private void checkValidityOfXmpMetadata(String xmlContent) throws Exception {
        xmlContent = xmlContent.replace("'", "\"");

        Pattern pattern = Pattern.compile(
                "<x:xmpmeta\\s*xmlns:x=\"adobe:ns:meta\\/\"(\\s\\S*=\"[^\"]*\")*>(.*)</x:xmpmeta>",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);

        if (!matcher.find()) {
            throw new Exception("Malformed XMPMETA Header");
        }
    }

    private void validateMetadataBasedOnSchema(Jpeg360Metadata element, Model jpeg360Model,
            Map<String, Resource> internalResourceMap)
            throws Exception {
        Optional<Statement> schemaStatement = ValidatorUtils.getOptionalValue(jpeg360Model, iri("adobe:ns:meta"),
                iri("http://ns.intel.com/umf/2.0schemas"));

        if (schemaStatement.isEmpty()) {
            throw new Exception("Schema doesn't exist");
        }

        Resource schemasContentsResource = internalResourceMap.get(schemaStatement.get().getObject().stringValue());
        List<Value> schemaDescriptorResourceNames = ValidatorUtils.getRdfBagContents(schemasContentsResource,
                jpeg360Model);

        Resource bag = getBagResourceFromJpeg360Element(schemaDescriptorResourceNames, jpeg360Model,
                internalResourceMap, false);

        List<Value> schemaContents = ValidatorUtils.getRdfBagContents(bag, jpeg360Model);

        Jpeg360MetadataValidator jpeg360MetadataValidator = new Jpeg360MetadataValidator(jpeg360Model,
                internalResourceMap);
        jpeg360MetadataValidator.validateMetadataBasedOnSchema(element, schemaContents);
    }

    private Jpeg360Metadata readMetadataContent(Model jpeg360Model, Map<String, Resource> internalResourceMap)
            throws Exception {
        Optional<Statement> metadataStatement = ValidatorUtils.getOptionalValue(jpeg360Model, iri("adobe:ns:meta"),
                iri("http://ns.intel.com/umf/2.0metadata"));

        Resource metadataContentsResource = internalResourceMap.get(metadataStatement.get().getObject().stringValue());
        List<Value> metadataDescriptorResourceNames = ValidatorUtils.getRdfBagContents(metadataContentsResource,
                jpeg360Model);

        Resource bag = getBagResourceFromJpeg360Element(metadataDescriptorResourceNames, jpeg360Model,
                internalResourceMap, true);

        if (bag == null) {
            Jpeg360Metadata jpeg360Metadata = new Jpeg360Metadata();
            jpeg360Metadata.setImageMetadata(new Jpeg360ImageMetadata());
            jpeg360Metadata.addViewport(new Jpeg360Viewport());
            return jpeg360Metadata;
        } else {
            List<Value> schemaContents = ValidatorUtils.getRdfBagContents(bag, jpeg360Model);
            Jpeg360MetadataValidator jpeg360MetadataValidator = new Jpeg360MetadataValidator(jpeg360Model,
                    internalResourceMap);
            return jpeg360MetadataValidator.readFromMetadata(schemaContents);
        }
    }

    private Resource getBagResourceFromJpeg360Element(List<Value> availableResourceNames, Model jpeg360Model,
            Map<String, Resource> internalResourceMap, boolean isMetadataElement) throws Exception {

        String iriTagName = isMetadataElement ? "http://ns.intel.com/umf/2.0set"
                : "http://ns.intel.com/umf/2.0descriptors";

        if (availableResourceNames.size() != 1) {
            throw new Exception("Only JPEG360Metadata structure expected");
        }

        Resource resource = internalResourceMap.get(availableResourceNames.get(0).stringValue());
        Optional<Statement> schemaStatement = ValidatorUtils.getSchemaStatement(jpeg360Model, resource);
        Optional<Statement> childStatement = ValidatorUtils.getOptionalValue(jpeg360Model,
                schemaStatement.get().getSubject(), iri(iriTagName));

        if (childStatement.isEmpty()) {
            throw new Exception(
                    String.format("Corrupted JPEG360Metadata structure. No %s element was found.", iriTagName));
        }

        return internalResourceMap.get(childStatement.get().getObject().stringValue());
    }
}

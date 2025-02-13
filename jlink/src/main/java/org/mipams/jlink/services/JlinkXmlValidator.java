package org.mipams.jlink.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mipams.jlink.entities.JlinkElement;
import org.mipams.jlink.entities.JlinkLink;
import org.mipams.jlink.entities.JlinkRegion;
import org.mipams.jlink.entities.JlinkScene;
import org.mipams.jlink.entities.JlinkViewport;
import org.mipams.jlink.entities.validator.JlinkValidator;
import org.mipams.jlink.entities.validator.ValidatorUtils;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.util.MipamsException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.ResourceFactory;
import org.springframework.stereotype.Service;


@Service
public class JlinkXmlValidator {

    private static final Logger logger = Logger.getLogger(JlinkXmlValidator.class.getName());

    public JlinkElement validateSchema(JumbfBox xmlContentTypeJumbfBox) throws MipamsException {
        XmlBox xmpBox = (XmlBox) xmlContentTypeJumbfBox.getContentBoxList().get(0);

        try {
            return validateXmlBoxContents(xmpBox);
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("JLINK Schema validation has failed: %s", e.getMessage()), e);
            throw new MipamsException(e);
        }
    }

    private JlinkElement validateXmlBoxContents(XmlBox xmpBox) throws Exception {

        byte[] rdfSchemaInBytes = stripXmpTags(xmpBox.getContent());

        try (InputStream is = new ByteArrayInputStream(rdfSchemaInBytes)) {
            Model jlinkModel = ModelFactory.createDefaultModel();
            jlinkModel.read(is, "");

            Property nextIdProperty = ResourceFactory.createProperty("http://ns.intel.com/umf/2.0", "next-id");

            StmtIterator nextIdStatement = jlinkModel.listStatements(null, nextIdProperty, (RDFNode) null);
            if (!nextIdStatement.hasNext()) {
                throw new Exception("JPEG 360 next id was not found");
            }

            Integer nextId = Integer.parseInt(nextIdStatement.next().getLiteral().toString());

            logger.log(Level.FINE, String.format("id: %d", nextId));

            Map<String, Resource> internalResourceMap = initializeResourceMap(jlinkModel);
            validateSchemasContent(jlinkModel, internalResourceMap);

            JlinkElement element = validateMetadataContent(jlinkModel, internalResourceMap);
            element.setNextId(nextId);

            return element;
        } catch (Exception e) {
            throw new Exception(String.format("Failed to validate JLINK metadata elements: %s", e.getMessage()), e);
        }
    }

    private Map<String, Resource> initializeResourceMap(Model jlinkModel) {
        Map<String, Resource> internalResourceMap = new HashMap<>();
        StmtIterator i = jlinkModel.listStatements();

        while(i.hasNext()) {
            Statement st = i.next();
            internalResourceMap.put(st.getSubject().toString(), st.getSubject());
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
            throw new Exception("JLINK Corrupted");
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
            throw new Exception("JLINK Corrupted");
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

    private void validateSchemasContent(Model jlinkModel, Map<String, Resource> internalResourceMap) throws Exception {
        Optional<Statement> schemaStatement = ValidatorUtils.getOptionalValue(jlinkModel, null,
                ResourceFactory.createProperty("http://ns.intel.com/umf/2.0schemas"));

        if (schemaStatement.isEmpty()) {
            throw new Exception("Schema doesn't exist");
        }

        Resource schemasContentsResource = internalResourceMap.get(schemaStatement.get().getObject().toString());
        List<Statement> schemaDescriptorResourceNames = ValidatorUtils.getRdfBagContents(schemasContentsResource,
                jlinkModel);

        JlinkValidator schemaValidator = new JlinkValidator(jlinkModel, internalResourceMap);
        schemaValidator.validateSchema(schemaDescriptorResourceNames);
    }

    private JlinkElement validateMetadataContent(Model jlinkModel, Map<String, Resource> internalResourceMap)
            throws Exception {
        Optional<Statement> metadataStatement = ValidatorUtils.getOptionalValue(jlinkModel, null,
            ResourceFactory.createProperty("http://ns.intel.com/umf/2.0metadata"));

        if (metadataStatement.isPresent()) {
            Resource metadataContentsResource = internalResourceMap
                    .get(metadataStatement.get().getObject().toString());
            List<Statement> metadataDescriptorResourceNames = ValidatorUtils.getRdfBagContents(metadataContentsResource,
                    jlinkModel);

            JlinkValidator schemaValidator = new JlinkValidator(jlinkModel, internalResourceMap);
            return schemaValidator.readFromMetadata(metadataDescriptorResourceNames);
        } else {
            JlinkElement defaultJlinkElement = new JlinkElement();

            JlinkScene defaultScene = new JlinkScene();
            defaultScene.addViewport(new JlinkViewport());

            defaultJlinkElement.setScene(defaultScene);
            defaultJlinkElement.setNextId(0);

            JlinkLink defaultLink = new JlinkLink();
            defaultLink.setRegion(new JlinkRegion());
            defaultJlinkElement.addLink(defaultLink);

            return defaultJlinkElement;
        }
    }
}

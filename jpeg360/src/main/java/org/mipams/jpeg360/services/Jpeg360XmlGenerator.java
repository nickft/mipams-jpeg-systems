package org.mipams.jpeg360.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mipams.jpeg360.entities.Jpeg360Metadata;
import org.mipams.jpeg360.entities.generator.Jpeg360MetadataGenerator;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.stereotype.Service;

@Service
public class Jpeg360XmlGenerator {
    private static final Logger logger = Logger.getLogger(Jpeg360XmlGenerator.class.getName());

    public XmlBox getXmlBoxFromJpeg360MetadataElement(Jpeg360Metadata element) throws MipamsException {
        try {
            String xmpContent = getXmpContent(element);
            XmlBox xmlBox = new XmlBox();
            xmlBox.setContent(xmpContent.getBytes());
            return xmlBox;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate JLINK XML schema", e);
            throw new MipamsException("Failed to generate JLINK XML schema", e);
        }
    }

    private String getXmpContent(Jpeg360Metadata element) throws Exception {
        Jpeg360MetadataGenerator metadataGenerator = new Jpeg360MetadataGenerator();

        StringBuilder xmpContent = new StringBuilder();
        xmpContent.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
        xmpContent.append(
                "<rdf:Description rdf:about=\"\" xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\" xmlns:umf=\"http://ns.intel.com/umf/2.0\">");

        if (element.getNextId() == null) {
            throw new Exception("JLINK metadata doesn't have a next-id assigned.");
        }

        xmpContent.append(String.format("<umf:next-id>%s</umf:next-id>", element.getNextId().toString()));

        xmpContent.append(metadataGenerator.schemaToRdfXml(element));
        xmpContent.append(metadataGenerator.metadataToRdfXml(element));

        xmpContent.append("</rdf:Description>");
        xmpContent.append("</rdf:RDF>");

        return xmpContent.toString();
    }
}

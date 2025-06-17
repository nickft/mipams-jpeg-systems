package org.mipams.jlink.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mipams.jlink.entities.JlinkElement;
import org.mipams.jlink.entities.generator.JlinkGenerator;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.stereotype.Service;

@Service
public class JlinkXmlGenerator {
    private static final Logger logger = Logger.getLogger(JlinkXmlGenerator.class.getName());

    public XmlBox getXmlBoxFromJlinkElement(JlinkElement element) throws MipamsException {
        try {
            String xmpContent = getXmpContent(element);
            // logger.info(xmpContent);
            XmlBox xmlBox = new XmlBox();
            xmlBox.setContent(xmpContent.getBytes());
            return xmlBox;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate JLINK XML schema", e);
            throw new MipamsException("Failed to generate JLINK XML schema", e);
        }
    }

    private String getXmpContent(JlinkElement element) throws Exception {
        JlinkGenerator jlinkGenerator = new JlinkGenerator();

        StringBuilder xmpContent = new StringBuilder();
        xmpContent.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
        xmpContent.append(
                "<rdf:Description rdf:about=\"\" xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\" " +
                        "xmlns:umf=\"http://ns.intel.com/umf/2.0\">");

        if (element.getNextId() == null) {
            throw new Exception("JLINK metadata doesn't have a next-id assigned.");
        }

        xmpContent.append(String.format("<umf:next-id>%s</umf:next-id>", element.getNextId().toString()));
        xmpContent.append(jlinkGenerator.schemaToRdfXml());

        if (element.getScene() != null) {
            xmpContent.append(jlinkGenerator.metadataToRdfXml(element));
        }

        xmpContent.append("</rdf:Description>");
        xmpContent.append("</rdf:RDF>");

        return xmpContent.toString();
    }
}

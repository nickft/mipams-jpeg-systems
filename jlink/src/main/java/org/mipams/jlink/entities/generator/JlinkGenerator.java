package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkElement;
import org.mipams.jlink.entities.Link;

public class JlinkGenerator extends JlinkElementAbstractGenerator<JlinkElement> {

    SceneGenerator sceneGenerator;
    LinkGenerator linkGenerator;

    public JlinkGenerator() {
        sceneGenerator = new SceneGenerator();
        linkGenerator = new LinkGenerator();
    }

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder jlinkSchema = new StringBuilder();
        jlinkSchema.append("<umf:schemas><rdf:Bag>");
        jlinkSchema.append(sceneGenerator.schemaToRdfXml());
        jlinkSchema.append(linkGenerator.schemaToRdfXml());
        jlinkSchema.append("</rdf:Bag></umf:schemas>");
        return jlinkSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkElement jlinkElement) throws Exception {
        StringBuilder jlinkSchema = new StringBuilder();
        jlinkSchema.append("<umf:metadata><rdf:Bag>");
        jlinkSchema.append(sceneGenerator.metadataToRdfXml(jlinkElement.getScene()));

        for (Link link : jlinkElement.getLinks()) {
            jlinkSchema.append(linkGenerator.metadataToRdfXml(link));
        }

        jlinkSchema.append("</rdf:Bag></umf:metadata>");
        return jlinkSchema.toString();
    }

}

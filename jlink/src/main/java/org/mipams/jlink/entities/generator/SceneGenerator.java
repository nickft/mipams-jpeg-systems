package org.mipams.jlink.entities.generator;

import org.mipams.jlink.entities.JlinkScene;
import org.mipams.jlink.entities.JlinkViewport;
import org.mipams.jlink.entities.validator.PropertyType;

public class SceneGenerator extends JlinkElementAbstractGenerator<JlinkScene> {

    private ViewportGenerator viewportGenerator;
    private ImageGenerator imageGenerator;

    public SceneGenerator() {
        imageGenerator = new ImageGenerator();
        viewportGenerator = new ViewportGenerator();
    }

    @Override
    public String schemaToRdfXml() throws Exception {
        StringBuilder sceneSchema = new StringBuilder(addResourceOpeningTag());

        sceneSchema.append(addSchema("Scene"));
        sceneSchema.append(addContentsOpeningTag());

        sceneSchema.append(addSchemaProperty("Version", PropertyType.STRING));
        sceneSchema.append(addSchemaProperty("Title", PropertyType.STRING));
        sceneSchema.append(addSchemaProperty("Note", PropertyType.STRING));

        sceneSchema.append(imageGenerator.schemaToRdfXml());
        sceneSchema.append(viewportGenerator.schemaToRdfXml());

        sceneSchema.append(addContentsClosingTag());
        sceneSchema.append(addResourceClosingTag());
        return sceneSchema.toString();
    }

    @Override
    public String metadataToRdfXml(JlinkScene scene) throws Exception {
        StringBuilder sceneMetadata = new StringBuilder(addResourceOpeningTag());
        sceneMetadata.append(addSchema("Scene"));
        sceneMetadata.append("<umf:set><rdf:Bag>");

        sceneMetadata.append(addMetadataProperty("Version", scene.getVersion()));
        sceneMetadata.append(addMetadataProperty("Title", scene.getTitle()));
        sceneMetadata.append(addMetadataProperty("Note", scene.getNote()));

        if (scene.getImage() == null) {
            throw new Exception("No schema is specified for scene");
        }

        sceneMetadata.append(imageGenerator.metadataToRdfXml(scene.getImage()));

        for (JlinkViewport vp : scene.getViewports()) {
            sceneMetadata.append(viewportGenerator.metadataToRdfXml(vp));
        }

        sceneMetadata.append("</rdf:Bag></umf:set>");
        sceneMetadata.append(addResourceClosingTag());

        return sceneMetadata.toString();
    }

}

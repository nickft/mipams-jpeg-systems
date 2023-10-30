package org.mipams.jpeg360.entities.generator;

import org.mipams.jpeg360.entities.Jpeg360AcceleratedRoi;
import org.mipams.jpeg360.entities.Jpeg360Metadata;
import org.mipams.jpeg360.entities.Jpeg360Viewport;

public class Jpeg360MetadataGenerator extends Jpeg360AbstractGenerator<Jpeg360Metadata> {

    Jpeg360ImageMetadataGenerator imageMetadataGenerator;
    Jpeg360ViewportGenerator viewportGenerator;
    Jpeg360AcceleraterRoiGenerator acceleraterRoiGenerator;

    public Jpeg360MetadataGenerator() {
        imageMetadataGenerator = new Jpeg360ImageMetadataGenerator();
        viewportGenerator = new Jpeg360ViewportGenerator();
        acceleraterRoiGenerator = new Jpeg360AcceleraterRoiGenerator();
    }

    @Override
    public String schemaToRdfXml(Jpeg360Metadata jpeg360Metadata) throws Exception {
        StringBuilder jpeg360Schema = new StringBuilder();
        jpeg360Schema.append("<umf:schemas><rdf:Bag>");
        jpeg360Schema.append(addResourceOpeningTag());
        jpeg360Schema.append(addSchema("JPEG360Metadata"));
        jpeg360Schema.append("<umf:descriptors><rdf:Bag>");

        jpeg360Schema.append(imageMetadataGenerator.schemaToRdfXml(jpeg360Metadata.getImageMetadata()));

        if (jpeg360Metadata.getImageMetadata() != null && jpeg360Metadata.getViewports().isEmpty()) {
            throw new Exception("At least one viewport shall be defined");
        }
        jpeg360Schema.append(viewportGenerator.schemaToRdfXml(jpeg360Metadata.getViewports().get(0)));

        if (!jpeg360Metadata.getAcceleratedROIs().isEmpty()) {
            jpeg360Schema.append(acceleraterRoiGenerator.schemaToRdfXml(jpeg360Metadata.getAcceleratedROIs().get(0)));
        }

        jpeg360Schema.append("</rdf:Bag></umf:descriptors>");
        jpeg360Schema.append(addResourceClosingTag());
        jpeg360Schema.append("</rdf:Bag></umf:schemas>");
        return jpeg360Schema.toString();
    }

    @Override
    public String metadataToRdfXml(Jpeg360Metadata jpeg360Metadata) throws Exception {
        StringBuilder jpeg360Schema = new StringBuilder();
        jpeg360Schema.append("<umf:metadata><rdf:Bag>");
        jpeg360Schema.append(addResourceOpeningTag());

        jpeg360Schema.append(addSchema("JPEG360Metadata"));
        jpeg360Schema.append("<umf:set>");

        if (jpeg360Metadata.getImageMetadata() != null) {
            jpeg360Schema.append("<rdf:Bag>");

            try {
                jpeg360Schema.append(imageMetadataGenerator.metadataToRdfXml(jpeg360Metadata.getImageMetadata()));

                for (Jpeg360Viewport viewport : jpeg360Metadata.getViewports()) {
                    jpeg360Schema.append(viewportGenerator.metadataToRdfXml(viewport));
                }

                for (Jpeg360AcceleratedRoi acceleratedRoi : jpeg360Metadata.getAcceleratedROIs()) {
                    jpeg360Schema.append(acceleraterRoiGenerator.metadataToRdfXml(acceleratedRoi));
                }
            } catch (Exception e) {
                throw new Exception("Failed to write provided element to JPEG 360 RDF metadata element");
            }
            jpeg360Schema.append("</rdf:Bag>");
        }

        jpeg360Schema.append("</umf:set>");
        jpeg360Schema.append(addResourceClosingTag());
        jpeg360Schema.append("</rdf:Bag></umf:metadata>");
        return jpeg360Schema.toString();
    }

}

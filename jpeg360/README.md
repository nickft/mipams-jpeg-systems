# MIPAMS JPEG Systems: JPEG 360 (ISO/IEC 19566-6)

This module introduces data structures for Omnidirectional (360-degree) image metadata, including viewports and accelerated Regions of Interest (ROI).

*Requires the `/jumbf` core module and **Apache JENA** for processing the underlying RDF/XML data model.*

---

## Usage Scenarios

Like JLINK, this extension converts domain objects to RDF/XML.

### Defining Viewports and Accelerated ROIs

```java
@Autowired
Jpeg360XmlGenerator jpeg360XmlGenerator;

public JumbfBox createJpeg360Metadata() throws Exception {
    Jpeg360Metadata jpeg360Metadata = new Jpeg360Metadata();
    
    // Base Image Metadata
    Jpeg360ImageMetadata imageMetadata = new Jpeg360ImageMetadata();
    imageMetadata.setVersion(2);
    jpeg360Metadata.setImageMetadata(imageMetadata);

    // Viewport configuration
    Jpeg360Viewport viewport = new Jpeg360Viewport();
    viewport.setUmfId(1);
    jpeg360Metadata.addViewport(viewport);

    // Accelerated ROI configuration
    Jpeg360AcceleratedRoi roi = new Jpeg360AcceleratedRoi();
    roi.setUmfId(2);
    roi.setBlockWidth(4);
    roi.setBlockHeight(4);
    roi.setAssociatedViewport(0);
    jpeg360Metadata.addAcceleratedRois(roi);

    // Generate RDF/XML Box
    XmlBox xmlBox = jpeg360XmlGenerator.getXmlBoxFromJpeg360MetadataElement(jpeg360Metadata);
    
    // Wrap the xmlBox via JumbfBoxBuilder...
    JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
    xmlContentBoxBuilder.appendContentBox(xmlBox);
    return xmlContentBoxBuilder.getResult();
}
```
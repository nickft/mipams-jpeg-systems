# MIPAMS JPEG Systems: JLINK (ISO/IEC 19566-7)

This module introduces structures to define interactive regions, navigational links, and scenes within a JPEG codestream. 

*Requires the `/jumbf` core module and **Apache JENA** for processing the underlying RDF/XML data model.*

---

## Usage Scenarios

The `JlinkXmlGenerator` converts object-oriented JLINK definitions into standard XML metadata boxes.

### Defining Scenes, Regions, and Links

```java
@Autowired
JlinkXmlGenerator jlinkXmlGenerator;

public JumbfBox createJlinkMetadata() throws Exception {
    // 1. Define Scene
    JlinkScene scene = new JlinkScene();
    JlinkImage image = new JlinkImage();
    image.setFormat("format");
    image.setHref("test");
    scene.setImage(image);

    // 2. Define Region & Link
    JlinkRegion region = new JlinkRegion();
    region.setX(12.0); region.setY(12.0); region.setW(12.0); region.setH(12.0);
    region.setShape("rectangle");

    JlinkLink link = new JlinkLink();
    link.setTo("destination_target");
    link.setRegion(region);
    link.setDuration(600);

    // 3. Generate RDF/XML Box
    JlinkElement element = new JlinkElement();
    element.setScene(scene);
    element.setLinks(List.of(link));
    
    XmlBox xmlBox = jlinkXmlGenerator.getXmlBoxFromJlinkElement(element);
    
    // Continue by wrapping xmlBox in a JumbfBoxBuilder...
    JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
    xmlContentBoxBuilder.appendContentBox(xmlBox);
    return xmlContentBoxBuilder.getResult();
}
```
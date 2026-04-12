# MIPAMS JPEG Systems: JUMBF (ISO/IEC 19566-5)

This module is the foundational Java reference implementation of the **JPEG Universal Metadata Box Format (JUMBF)**. All other extensions in the MIPAMS JPEG Systems project depend on this core library.

## Maven Dependency

If you are using this in a standalone Spring application:

```xml
<dependency>
    <groupId>org.mipams</groupId>
    <artifactId>jumbf</artifactId>
    <version>YOUR_VERSION</version>
</dependency>
```

---

## Usage Scenarios

The JUMBF Core module provides multiple options for Generator and Parser services depending on your target media. The following examples demonstrate how to process JUMBF data within **typical JPEG 1 files**, which is the most common use case. 

This is achieved using the `JpegCodestreamGenerator` and `JpegCodestreamParser`, which are implemented as Spring-managed Java Beans.

### 1. Embedding JUMBF in a JPEG 1 File

To embed metadata into a standard JPEG 1 image, you build your JUMBF boxes and pass them along with the source image to the codestream generator.

```java
@Autowired
private JpegCodestreamGenerator jpegCodestreamGenerator;

public void embedMetadata() throws MipamsException {
    // 1. Create a content box (e.g., a simple JSON payload)
    JsonBox jsonBox = new JsonBox();
    jsonBox.setContent("{\"message\": \"Hello World\"}".getBytes());

    // 2. Wrap it in a JUMBF Box
    JumbfBoxBuilder builder = new JumbfBoxBuilder(new JsonContentType());
    builder.appendContentBox(jsonBox);
    JumbfBox jumbfBox = builder.getResult();

    // 3. Inject the JUMBF Box into a standard JPEG 1 codestream
    jpegCodestreamGenerator.generateJumbfMetadataToFile(
        List.of(jumbfBox),
        "input_standard.jpeg",
        "output_with_jumbf.jpeg"
    );
}
```

### 2. Extracting JUMBF from a JPEG 1 File

To read metadata back out of a typical JPEG 1 codestream, simply pass the file to the codestream parser. It will scan the APP11 markers and reconstruct the object model.

```java
@Autowired
private JpegCodestreamParser jpegCodestreamParser;

public void readMetadata() throws MipamsException {
    // 1. Parse the standard JPEG 1 file to extract all embedded JUMBF boxes
    List<JumbfBox> boxes = jpegCodestreamParser.parseMetadataFromFile("output_with_jumbf.jpeg");
    
    // 2. Process the extracted data
    System.out.println("Successfully extracted " + boxes.size() + " JUMBF boxes.");
}
```
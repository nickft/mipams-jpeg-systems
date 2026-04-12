# MIPAMS JPEG Systems: JPEG Snack (ISO/IEC 19566-8)

This module extends the core JUMBF SDK to support media compositions, animations, and object instructions over time using the JPEG Snack constructs.

*Requires the `/jumbf` core module.*

---

## Usage Scenarios

JPEG Snack handles multiple media objects and dictates how they are composed. 

### Preparing a JPEG Snack Box

```java
public JumbfBox prepareJpegSnackJumbfBox() throws MipamsException {
    JumbfBoxBuilder jpegSnack = new JumbfBoxBuilder(new JpegSnackContentType());

    // 1. Description Box
    JpegSnackDescriptionBox jsdb = new JpegSnackDescriptionBox();
    jsdb.setVersion(1);
    jsdb.setStartTime(2000);
    Composition composition = new Composition();
    composition.setNoOfObjects(1);
    composition.getObjectIds().add(1);
    jsdb.getCompositions().add(composition);

    // 2. Instruction Set Box (Animations)
    InstructionSetBox issb = new InstructionSetBox();
    issb.setRepetition(1);
    issb.setTick(1000);
    InstructionParameter parameter = new InstructionParameter();
    parameter.setLife(12);
    parameter.setWidth(2);
    parameter.setHeight(2);
    issb.getInstructionParameters().add(parameter);

    // 3. Object Metadata Box (Media Linking)
    ObjectMetadataBox obmb = new ObjectMetadataBox();
    obmb.setId(1);
    obmb.setMediaType("image/jpg");
    obmb.getLocations().add("self#jumbf=Object1");

    jpegSnack.appendContentBox(jsdb);
    jpegSnack.appendContentBox(issb);
    jpegSnack.appendContentBox(obmb);

    return jpegSnack.getResult();
}
```
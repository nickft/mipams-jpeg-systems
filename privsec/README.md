# MIPAMS JPEG Systems: Privacy and Security (ISO/IEC 19566-4)

This submodule extends the core JUMBF SDK to enable privacy- and security-aware metadata handling inside JPEG codestreams. It introduces the **Protection JUMBF Box** and **Replacement JUMBF Box**.

*Requires the `/jumbf` core module.*

---

## Usage Scenarios

This module allows you to signal cryptographic protection schemes and structured replacement content.

### AES-256-CBC Protection Box Example

```java
public void createProtectedBox() throws Exception {
    ProtectionDescriptionBox protectionDescriptionBox = new ProtectionDescriptionBox();
    
    // Configure encryption scheme
    protectionDescriptionBox.setAes256CbcProtection();
    protectionDescriptionBox.updateFieldsBasedOnExistingData();

    // Wrap in a Protection JUMBF Box
    JumbfBox givenJumbfBox = getProtectionJumbfBoxBasedOnProtectionDescriptionBox(protectionDescriptionBox);

    // Mock lifecycle: Generate to file and parse back
    JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

    assertEquals(givenJumbfBox, parsedJumbfBox);
}
```
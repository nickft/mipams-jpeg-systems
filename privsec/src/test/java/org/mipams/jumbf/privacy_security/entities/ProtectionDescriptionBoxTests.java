package org.mipams.jumbf.privacy_security.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox.MethodType;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProtectionDescriptionBoxTests {

    @Test
    void testGetMethodTypeFromString() throws MipamsException {
        String external = MethodType.EXTERNAL.getMethodType();
        assertEquals(MethodType.EXTERNAL, MethodType.getMethodTypeFromString(external));
    }

    @Test
    void testGetMethodTypeFromNonExistentString() {
        String nonExistentType = "test";

        Exception e = assertThrows(MipamsException.class, () -> {
            MethodType.getMethodTypeFromString(nonExistentType);
        });

        String expectedMessage = String.format("Method is not supported. Supported methods are: %s, %s, %s",
                MethodType.EXTERNAL.getMethodType(),
                MethodType.AES_256_CBC.getMethodType(), MethodType.AES_256_CBC_WITH_IV.getMethodType());

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void testIsAes256CbcProtection() {
        ProtectionDescriptionBox box = new ProtectionDescriptionBox();
        box.setAes256CbcProtection();

        assertTrue(box.isAes256CbcProtection());

        box.setAes256CbcWithIvProtection();

        assertFalse(box.isAes256CbcProtection());
    }

}

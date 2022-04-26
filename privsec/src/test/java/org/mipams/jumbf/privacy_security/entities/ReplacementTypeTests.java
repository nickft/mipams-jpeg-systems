package org.mipams.jumbf.privacy_security.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReplacementTypeTests {
    @Test
    void testGetMethodTypeFromString() throws MipamsException {
        String app = ReplacementType.APP.getType();
        assertEquals(ReplacementType.APP, ReplacementType.getTypeFromString(app));
    }

    @Test
    void testGetMethodTypeFromNonExistentString() {
        String nonExistentType = "test";

        Exception e = assertThrows(MipamsException.class, () -> {
            ReplacementType.getTypeFromString(nonExistentType);
        });

        String expectedMessage = String.format("Method is not supported. Supported methods are: %s, %s, %s, %s",
                ReplacementType.BOX.getType(), ReplacementType.APP.getType(), ReplacementType.ROI.getType(),
                ReplacementType.FILE.getType());

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void testGetMethodTypeFromNonExistentId() {
        int nonExistentId = -1;

        Exception e = assertThrows(MipamsException.class, () -> {
            ReplacementType.getTypeFromId(nonExistentId);
        });

        String expectedMessage = String.format("Method is not supported. Supported methods are: %s, %s, %s, %s",
                ReplacementType.BOX.getType(), ReplacementType.APP.getType(), ReplacementType.ROI.getType(),
                ReplacementType.FILE.getType());

        assertEquals(expectedMessage, e.getMessage());
    }
}

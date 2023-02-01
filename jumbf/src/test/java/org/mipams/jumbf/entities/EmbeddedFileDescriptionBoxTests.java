package org.mipams.jumbf.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class EmbeddedFileDescriptionBoxTests {

    @Test
    void testSetMediaTypeFromStringWithIncorrectInpu() {
        EmbeddedFileDescriptionBox dBox = new EmbeddedFileDescriptionBox();
        Exception e = assertThrows(MipamsException.class, () -> {
            dBox.setMediaTypeFromString("test");
        });

        assertEquals("Bad Media Type", e.getMessage());
    }

    @Test
    void testSetMediaTypeFromStringWithNullInput() {
        EmbeddedFileDescriptionBox dBox = new EmbeddedFileDescriptionBox();
        Exception e = assertThrows(MipamsException.class, () -> {
            dBox.setMediaTypeFromString(null);
        });

        assertEquals("Bad Media Type", e.getMessage());
    }

}

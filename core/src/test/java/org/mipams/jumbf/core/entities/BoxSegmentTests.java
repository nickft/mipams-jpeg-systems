package org.mipams.jumbf.core.entities;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoxSegmentTests {

    @Test
    void testSetMediaTypeFromStringWithIncorrectInpu() {
        BoxSegment b1 = new BoxSegment(1, 1, 1, 1, 1, null, "payloadUrl");
        BoxSegment b2 = new BoxSegment(1, 1, 1, 1, 1, null, "payloadUrl");
        assertTrue(b1.compareTo(b2) == 0);

        b1 = new BoxSegment(1, 1, 1, 1, 1, null, "payloadUrl");
        b2 = new BoxSegment(1, 1, 2, 1, 1, null, "payloadUrl");

        assertTrue(b1.compareTo(b2) < 0);
        assertTrue(b2.compareTo(b1) > 0);
    }

}

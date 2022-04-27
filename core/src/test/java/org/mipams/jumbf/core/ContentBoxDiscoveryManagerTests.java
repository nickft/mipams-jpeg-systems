package org.mipams.jumbf.core;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ContentBoxDiscoveryManagerTests {

    ContentTypeDiscoveryManager manager;

    @BeforeEach
    void setup() {
        JsonContentType service = new JsonContentType();

        manager = new ContentTypeDiscoveryManager();
        ReflectionTestUtils.setField(manager, "contentTypeServiceList", List.of(service));
    }

    @Test
    void testGetServiceBasedOnRandomUuid() {

        String randomInput = UUID.randomUUID().toString();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getContentBoxServiceBasedOnContentUuid(randomInput);
        });

        String expectedMessage = String.format("Box with uuid %s is not a Content Box", randomInput.toString());

        assertEquals(expectedMessage, exception.getMessage());
    }
}

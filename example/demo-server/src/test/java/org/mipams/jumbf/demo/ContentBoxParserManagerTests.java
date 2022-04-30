package org.mipams.jumbf.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.core.JsonBoxParser;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class ContentBoxParserManagerTests {

    ContentTypeParserManager manager;

    @BeforeEach
    void setup() {
        JsonContentType service = new JsonContentType();

        JsonBoxParser parser = new JsonBoxParser();
        ReflectionTestUtils.setField(parser, "jsonContentType", service);

        manager = new ContentTypeParserManager();
        ReflectionTestUtils.setField(manager, "contentBoxParserList", List.of(parser));
    }

    @Test
    void testGetServiceBasedOnRandomUuid() {

        String randomInput = UUID.randomUUID().toString();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getParserBasedOnContentUUID(randomInput);
        });

        String expectedMessage = String.format("Box with uuid %s is not a Content Box", randomInput);

        assertEquals(expectedMessage, exception.getMessage());
    }
}

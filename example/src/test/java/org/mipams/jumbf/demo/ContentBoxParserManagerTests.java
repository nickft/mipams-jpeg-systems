package org.mipams.jumbf.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.ContentBoxDiscoveryManager;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.JsonBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.core.JsonBoxParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class ContentBoxParserManagerTests {

    ContentBoxParserManager manager;

    @BeforeEach
    void setup() {
        JsonBoxService service = new JsonBoxService();
        JsonBox jsonBox = new JsonBox();
        ReflectionTestUtils.setField(service, "serviceMetadata",
                new ServiceMetadata(jsonBox.getTypeId(), jsonBox.getType(), jsonBox.getContentTypeUUID()));

        ContentBoxDiscoveryManager contentBoxManager = new ContentBoxDiscoveryManager();
        ReflectionTestUtils.setField(contentBoxManager, "contentBoxServiceList", List.of(service));

        JsonBoxParser parser = new JsonBoxParser();
        ReflectionTestUtils.setField(parser, "jsonBoxService", service);

        manager = new ContentBoxParserManager();
        ReflectionTestUtils.setField(manager, "contentBoxDiscoveryManager", contentBoxManager);
        ReflectionTestUtils.setField(manager, "contentBoxParserList", List.of(parser));
    }

    @Test
    void testGetServiceBasedOnRandomUuid() {

        UUID randomInput = UUID.randomUUID();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getParserBasedOnContentUUID(randomInput.toString());
        });

        String expectedMessage = String.format("Box with uuid %s is not a Content Box", randomInput.toString());

        assertEquals(expectedMessage, exception.getMessage());
    }
}

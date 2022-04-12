package org.mipams.jumbf.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.services.ContentBoxService;
import org.mipams.jumbf.core.services.JsonBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("rawtypes")
public class ContentBoxDiscoveryManagerTests {

    ContentBoxDiscoveryManager manager;

    @BeforeEach
    void setup() {
        List<ContentBoxService> mockContentBoxServiceList = new ArrayList<>();

        JsonBoxService service = new JsonBoxService();
        mockContentBoxServiceList.add(service);

        manager = new ContentBoxDiscoveryManager();
        ReflectionTestUtils.setField(manager, "contentBoxServiceList", mockContentBoxServiceList);
    }

    @Test
    void testGetServiceBasedOnRandomUuid() {

        UUID randomInput = UUID.randomUUID();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getContentBoxServiceBasedOnContentUUID(randomInput);
        });

        String expectedMessage = String.format("Box with uuid %s is not a Content Box", randomInput.toString());

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testGetMetadataForContentServiceWithRandomType() {
        String randomInput = CoreUtils.randomStringGenerator();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getMetadataForContentBoxServiceWithType(randomInput);
        });

        String expectedMessage = String.format("Box %s is not supported yet", randomInput);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testGetContentBoxServiceBasedOnBoxWithRandomId() {
        int nonExistentInput = 5;

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getContentBoxServiceBasedOnBoxWithId(nonExistentInput);
        });

        String expectedMessage = String.format("Box type with id: 0x%s is not supported yet", nonExistentInput);

        assertEquals(expectedMessage, exception.getMessage());
    }
}

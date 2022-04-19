package org.mipams.jumbf.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.JsonBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ContentBoxDiscoveryManagerTests {

    ContentBoxDiscoveryManager manager;

    @BeforeEach
    void setup() {
        JsonBoxService service = new JsonBoxService();
        JsonBox jsonBox = new JsonBox();
        ReflectionTestUtils.setField(service, "serviceMetadata",
                new ServiceMetadata(jsonBox.getTypeId(), jsonBox.getType(), jsonBox.getContentTypeUUID()));

        manager = new ContentBoxDiscoveryManager();
        ReflectionTestUtils.setField(manager, "contentBoxServiceList", List.of(service));
    }

    @Test
    void testGetServiceBasedOnRandomUuid() {

        UUID randomInput = UUID.randomUUID();

        Exception exception = assertThrows(MipamsException.class, () -> {
            manager.getContentBoxServiceBasedOnContentUUID(randomInput.toString());
        });

        String expectedMessage = String.format("Box with uuid %s is not a Content Box", randomInput.toString());

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

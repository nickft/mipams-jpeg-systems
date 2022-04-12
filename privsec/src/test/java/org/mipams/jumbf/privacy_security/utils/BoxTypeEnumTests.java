package org.mipams.jumbf.privacy_security.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoxTypeEnumTests {

    @Test
    void testGetBoxTypeAsStringFromId() {
        String testEnum = BoxTypeEnum.getBoxTypeAsStringFromId(0x6A756D62);
        assertNotNull(testEnum);
    }

    @Test
    void testGetBoxTypeFromContentUuidOrNull() {
        BoxTypeEnum testEnum = BoxTypeEnum
                .getBoxTypeFromContentUuidOrNull(BoxTypeEnum.ContiguousCodestreamBox.getContentUuid());
        assertNotNull(testEnum);
    }

    @Test
    void testGetBoxTypeFromRandomUuid() {
        BoxTypeEnum testEnum = BoxTypeEnum.getBoxTypeFromContentUuidOrNull(UUID.randomUUID());
        assertNull(testEnum);
    }

    @Test
    void testGetBoxTypeAsStringFromNonExistentId() {
        String testEnum = BoxTypeEnum.getBoxTypeAsStringFromId(0);
        assertEquals("", testEnum);
    }

    @Test
    void testGetBoxTypeFromString() {
        BoxTypeEnum testEnum = BoxTypeEnum.getBoxTypeFromString(BoxTypeEnum.ContiguousCodestreamBox.getType());
        assertNotNull(testEnum);
    }

    @Test
    void testGetBoxTypeFromNull() {
        BoxTypeEnum testEnum = BoxTypeEnum.getBoxTypeFromString(null);
        assertNull(testEnum);
    }

    @Test
    void testGetMetadata() {
        BoxTypeEnum type = BoxTypeEnum.BinaryDataBox;
        ServiceMetadata serviceMetadata = new ServiceMetadata(type.getTypeId(), type.getType(), type.getContentUuid());

        assertEquals(serviceMetadata.toString(), BoxTypeEnum.BinaryDataBox.getServiceMetadata().toString());
    }
}

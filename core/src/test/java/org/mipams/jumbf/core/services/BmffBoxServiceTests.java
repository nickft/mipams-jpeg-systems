
package org.mipams.jumbf.core.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.test.util.ReflectionTestUtils;

public class BmffBoxServiceTests {

    @Test
    void testInequalityInBoxSizeAndHeaderSize() throws MipamsException {
        JsonBoxService jsonBoxService = new JsonBoxService();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl("test");
        jsonBox.updateBmffHeadersBasedOnBox();

        jsonBox.setLBox(-1);
        Exception e = assertThrows(MipamsException.class, () -> {
            jsonBoxService.verifyBoxSizeEqualsToSizeSpecifiedInBmffHeaders(jsonBox);
        });

        assertTrue(e.getMessage().startsWith("Mismatch in the byte counting"));
    }

    @Test
    void testProvidingWrongTboxForService() throws IOException {
        UuidBoxService uuidBoxService = new UuidBoxService();

        UuidBox uuidBox = new UuidBox();
        ServiceMetadata serviceMetadata = new ServiceMetadata(uuidBox.getTypeId(), uuidBox.getType(),
                uuidBox.getContentTypeUUID());

        ReflectionTestUtils.setField(uuidBoxService, "serviceMetadata", serviceMetadata);

        byte[] bmffHeaderInput = new byte[8];

        System.arraycopy(CoreUtils.convertIntToByteArray(8), 0, bmffHeaderInput, 0, 4);
        System.arraycopy(CoreUtils.convertIntToByteArray(uuidBox.getTypeId() + 3), 0, bmffHeaderInput, 4, 4);

        try (InputStream input = new ByteArrayInputStream(bmffHeaderInput);) {
            assertThrows(MipamsException.class, () -> {
                uuidBoxService.parseFromJumbfFile(input, 8);
            });
        }
    }
}

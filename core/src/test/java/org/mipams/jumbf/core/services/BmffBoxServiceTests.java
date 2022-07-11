
package org.mipams.jumbf.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.services.boxes.JsonBoxService;
import org.mipams.jumbf.core.services.boxes.UuidBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class BmffBoxServiceTests {

    @Test
    void testInequalityInBoxSizeAndHeaderSize() throws MipamsException {
        JsonBoxService jsonBoxService = new JsonBoxService();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent("test".getBytes());
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
        ServiceMetadata serviceMetadata = new ServiceMetadata(uuidBox.getTypeId(), uuidBox.getType());

        ReflectionTestUtils.setField(uuidBoxService, "serviceMetadata", serviceMetadata);

        byte[] bmffHeaderInput = new byte[8];

        System.arraycopy(CoreUtils.convertIntToByteArray(8), 0, bmffHeaderInput, 0, 4);
        System.arraycopy(CoreUtils.convertIntToByteArray(uuidBox.getTypeId() + 3), 0, bmffHeaderInput, 4, 4);

        ParseMetadata parseMetadata = new ParseMetadata();
        parseMetadata.setAvailableBytesForBox(8);

        try (InputStream input = new ByteArrayInputStream(bmffHeaderInput);) {
            assertThrows(MipamsException.class, () -> {
                uuidBoxService.parseFromJumbfFile(input, parseMetadata);
            });
        }
    }

    @Test
    void testParsingLBox() throws IOException {
        UuidBoxService uuidBoxService = new UuidBoxService();

        UuidBox uuidBox = new UuidBox();
        ServiceMetadata serviceMetadata = new ServiceMetadata(uuidBox.getTypeId(), uuidBox.getType());

        ReflectionTestUtils.setField(uuidBoxService, "serviceMetadata", serviceMetadata);

        byte[] bmffHeaderInput = new byte[16];

        long xBoxValue = Long.valueOf(5);

        System.arraycopy(CoreUtils.convertIntToByteArray(1), 0, bmffHeaderInput, 0, 4);
        System.arraycopy(CoreUtils.convertIntToByteArray(uuidBox.getTypeId()), 0, bmffHeaderInput, 4, 4);
        System.arraycopy(CoreUtils.convertLongToByteArray(xBoxValue), 0, bmffHeaderInput, 8, 8);

        ParseMetadata parseMetadata = new ParseMetadata();
        parseMetadata.setAvailableBytesForBox(16);

        try (InputStream input = new ByteArrayInputStream(bmffHeaderInput);) {
            assertThrows(MipamsException.class, () -> {
                uuidBoxService.parseFromJumbfFile(input, parseMetadata);
            });
        }
    }

    @Test
    void testUpdatingBmffHeadersOfLongBox() throws MipamsException {
        MockLongBox mockBox = new MockLongBox();

        mockBox.updateBmffHeadersBasedOnBox();

        long expectedSize = CoreUtils.INT_BYTE_SIZE * 2 + CoreUtils.LONG_BYTE_SIZE + mockBox.PAYLOAD_SIZE;

        assertTrue(mockBox.getLBox() == 1);
        assertEquals(expectedSize, mockBox.getXlBox());
    }

    @Test
    void testConditionOfXBoxEnabledIfLBoxIsNot1() throws MipamsException {
        MockLongBox mockBox = new MockLongBox();

        mockBox.setLBox(34);
        mockBox.setXlBox(Long.valueOf(Integer.MAX_VALUE));

        assertTrue(!mockBox.isXBoxEnabled());
    }

    @Test
    void testConditionOfXBoxEnabledIfXBoxIsNull() throws MipamsException {
        MockLongBox mockBox = new MockLongBox();

        mockBox.setLBox(1);
        mockBox.setXlBox(null);

        assertTrue(!mockBox.isXBoxEnabled());
    }

    public class MockLongBox extends BmffBox {

        public long PAYLOAD_SIZE = Long.parseLong("FFFFFFFF", 16);

        @Override
        public int getTypeId() {
            return 0x67574832;
        }

        @Override
        public String getType() {
            return "mock";
        }

        @Override
        protected long calculatePayloadSize() throws MipamsException {
            return PAYLOAD_SIZE;
        }

    }
}

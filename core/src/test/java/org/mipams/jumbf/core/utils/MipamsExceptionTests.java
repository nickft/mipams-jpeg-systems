package org.mipams.jumbf.core.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MipamsExceptionTests {

    @Test
    void testClassInit() {
        MipamsException exception = new MipamsException("Hi");
        assertNotNull(exception);
    }
}

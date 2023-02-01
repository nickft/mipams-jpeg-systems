package org.mipams.jumbf.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.util.MipamsException;

public class MipamsExceptionTests {

    @Test
    void testClassInit() {
        MipamsException exception = new MipamsException("Hi");
        assertNotNull(exception);
    }
}

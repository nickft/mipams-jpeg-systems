package org.mipams.jumbf.core.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.BadRequestException;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BadRequestExceptionTests {

    @Test
    void testEmptyConstructor() {
        BadRequestException exception = new BadRequestException();
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithMessage() {
        BadRequestException exception = new BadRequestException("Hi");
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithThrowable() {
        BadRequestException exception = new BadRequestException(new IOException());
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithThrowableAndMessage() {
        BadRequestException exception = new BadRequestException("Hi", new IOException());
        assertNotNull(exception);
    }
}

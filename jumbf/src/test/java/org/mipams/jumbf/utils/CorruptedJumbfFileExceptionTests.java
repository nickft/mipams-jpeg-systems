package org.mipams.jumbf.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mipams.jumbf.util.CorruptedJumbfFileException;

public class CorruptedJumbfFileExceptionTests {

    @Test
    void testEmptyConstructor() {
        CorruptedJumbfFileException exception = new CorruptedJumbfFileException();
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithMessage() {
        CorruptedJumbfFileException exception = new CorruptedJumbfFileException("Hi");
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithThrowable() {
        CorruptedJumbfFileException exception = new CorruptedJumbfFileException(new IOException());
        assertNotNull(exception);
    }

    @Test
    void testConstructorWithThrowableAndMessage() {
        CorruptedJumbfFileException exception = new CorruptedJumbfFileException("Hi", new IOException());
        assertNotNull(exception);
    }
}

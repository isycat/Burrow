package com.isycat.burrow;

import com.isycat.burrow.error.ClientError;
import com.isycat.burrow.error.ServerError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperationErrorTest {
    @Test
    public void clientErrorSerialization() {
        final ClientError error = new ClientError(
                456,
                "error.wrongness",
                "something wronged okay");
        final String expectedString =
                "{" +
                    "\"status\":456," +
                    "\"errorCode\":\"error.wrongness\"," +
                    "\"errorMessage\":\"something wronged okay\"" +
                "}";
        assertEquals("Error during serialization",
                expectedString, error.toString());
    }

    @Test
    public void serverErrorSerialization() {
        final ServerError error = new ServerError(
                567,
                "error.wrongness",
                "something wronged okay");
        final String expectedString =
                "{" +
                    "\"status\":567," +
                    "\"errorCode\":\"error.wrongness\"," +
                    "\"errorMessage\":\"something wronged okay\"" +
                "}";
        assertEquals("Error during serialization",
                expectedString, error.toString());
    }
}

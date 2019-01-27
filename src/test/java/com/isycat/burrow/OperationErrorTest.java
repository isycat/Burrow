package com.isycat.burrow;

import com.isycat.burrow.error.ClientError;
import com.isycat.burrow.error.ServerError;
import com.isycat.burrow.operation.OperationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperationErrorTest {
    @Test
    public void clientErrorSerialization() {
        final String expectedString =
                "{" +
                    "\"status\":456," +
                    "\"errorCode\":\"error.wrongness\"," +
                    "\"errorMessage\":\"something wronged okay\"," +
                    "\"requestId\":\"" + OperationContext.getRequestId() + "\"" +
                "}";
        final ClientError error = new ClientError(
                456,
                "error.wrongness",
                "something wronged okay");
        assertEquals("Error during serialization",
                expectedString, error.toString());
    }

    @Test
    public void serverErrorSerialization() {
        final String expectedString =
                "{" +
                    "\"status\":567," +
                    "\"errorCode\":\"error.wrongness\"," +
                    "\"errorMessage\":\"something wronged okay\"," +
                    "\"requestId\":\"" + OperationContext.getRequestId() + "\"" +
                "}";
        final ServerError error = new ServerError(
                567,
                "error.wrongness",
                "something wronged okay",
                null);
        assertEquals("Error during serialization",
                expectedString, error.toString());
    }
}

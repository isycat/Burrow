package com.isycat.burrow.error;

public class UnknownOperationError extends ClientError {
    public static final String CODE = "Client.UnknownOperation";

    public UnknownOperationError(final String operation) {
        super(404,
                CODE,
                String.format("Unknown operation '%s'", operation));
    }

    public UnknownOperationError() {
        super(404,
                CODE,
                "Unknown operation");
    }
}

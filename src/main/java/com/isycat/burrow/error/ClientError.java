package com.isycat.burrow.error;

public class ClientError extends OperationError {
    public ClientError(final String errorMessage) {
        super(400, "client.generic", errorMessage);
    }

    public ClientError(final String errorCode,
                       final String errorMessage) {
        super(400, errorCode, errorMessage);
    }

    public ClientError(final int status,
                       final String errorCode,
                       final String errorMessage) {
        super(status, errorCode, errorMessage);
    }

    @Override
    public boolean isServerError() {
        return false;
    }

    @Override
    public boolean isClientError() {
        return true;
    }
}

package com.isycat.burrow.error;

public class ServerError extends OperationError {
    public ServerError(final String errorMessage) {
        super(400, "server.generic", errorMessage);
    }

    public ServerError(final String errorCode,
                       final String errorMessage,
                       final Throwable cause) {
        this(400, errorCode, errorMessage, cause);
    }

    public ServerError(final int status,
                       final String errorCode,
                       final String errorMessage,
                       final Throwable cause) {
        super(status, errorCode, errorMessage, cause);
    }

    @Override
    public boolean isServerError() {
        return true;
    }

    @Override
    public boolean isClientError() {
        return false;
    }
}

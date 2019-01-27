package com.isycat.burrow;

public class ClientError extends RuntimeException {
    private final int status;
    private final String errorCode;
    private final String errorMessage;

    public ClientError(final int status,
                       final String errorCode,
                       final String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}

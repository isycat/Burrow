package com.isycat.burrow.error;

import com.google.gson.annotations.Expose;
import com.isycat.burrow.operation.OperationContext;
import com.isycat.burrow.serialization.StrictSerializer;

public abstract class OperationError extends RuntimeException {
    @Expose protected final int status;
    @Expose protected final String errorCode;
    @Expose protected final String errorMessage;
    @Expose protected final String requestId;

    public OperationError(final int status,
                          final String errorCode,
                          final String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.requestId = OperationContext.getRequestId();
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public abstract boolean isServerError();

    public abstract boolean isClientError();

    @Override
    public String toString() {
        return StrictSerializer.serialize(this);
    }
}

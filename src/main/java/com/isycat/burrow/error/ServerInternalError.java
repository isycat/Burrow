package com.isycat.burrow.error;

public class ServerInternalError extends ServerError {

    public ServerInternalError(final Exception cause) {
        super(500,
                "server.internal",
                "An internal error occurred.",
                cause);
    }

    public ServerInternalError(final String message, final Exception cause) {
        super(500,
                "server.internal",
                message,
                cause);
    }
}

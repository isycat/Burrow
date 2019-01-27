package com.isycat.burrow.error;

public class ServerInternalError extends ServerError {
    public static final String CODE = "Server.Internal";

    public ServerInternalError(final Exception cause) {
        super(500,
                CODE,
                "An internal error occurred.",
                cause);
    }

    public ServerInternalError(final String message, final Exception cause) {
        super(500,
                CODE,
                message,
                cause);
    }
}

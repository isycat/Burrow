package com.isycat.burrow.error;

public class MissingParameterError extends ClientError {
    public static final String CODE = "Client.MissingParameter";

    public MissingParameterError(final String parameter) {
        super(CODE, String.format(
                "Missing parameter '%s'",
                parameter
        ));
    }
}

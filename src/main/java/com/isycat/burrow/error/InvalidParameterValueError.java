package com.isycat.burrow.error;

public class InvalidParameterValueError extends ClientError {
    public static final String CODE = "Client.InvalidParameterValue";

    public InvalidParameterValueError(final String parameter) {
        super(CODE, String.format(
                "Invalid parameter value for parameter '%s'",
                parameter
        ));
    }

    public InvalidParameterValueError(final String parameter, final String value) {
        super(CODE, String.format(
                "Invalid parameter value for parameter '%s', '%s'",
                parameter,
                value
        ));
    }
}

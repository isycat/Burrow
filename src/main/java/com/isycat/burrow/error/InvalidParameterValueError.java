package com.isycat.burrow.error;

public class InvalidParameterValueError extends ClientError {
    public InvalidParameterValueError(final String parameter) {
        super("client.invalidparametervalue", String.format(
                "Invalid parameter value for parameter '%s'",
                parameter
        ));
    }

    public InvalidParameterValueError(final String parameter, final String value) {
        super("client.invalidparametervalue", String.format(
                "Invalid parameter value for parameter '%s', '%s'",
                parameter,
                value
        ));
    }
}

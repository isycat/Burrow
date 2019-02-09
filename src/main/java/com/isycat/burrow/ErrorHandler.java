package com.isycat.burrow;

import com.isycat.burrow.error.OperationError;

import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface ErrorHandler {
    ErrorHandler DEFAULT = (operationError, httpResponse) -> {
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().println(operationError);
    };

    void handleError(final OperationError error, final HttpServletResponse response) throws Exception;
}
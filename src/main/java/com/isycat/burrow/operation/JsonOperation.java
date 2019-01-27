package com.isycat.burrow.operation;

import com.isycat.burrow.HttpConstants;
import com.isycat.burrow.error.ServerError;
import com.isycat.burrow.json.JsonRequest;
import com.isycat.burrow.json.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class JsonOperation<RequestType extends JsonRequest, ResponseType extends JsonResponse>
        extends AbstractServletOperationHandler<RequestType, ResponseType> {
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    public static final ServerError SERVER_INTERNAL =
            new ServerError(500,
                    "server.internal",
                    "An internal error occurred.");

    @Override
    public final ResponseType getResponse(final RequestType request,
                                    final HttpServletRequest servletRequest,
                                    final HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType(APPLICATION_JSON_CONTENT_TYPE);
        final ResponseType response = this.handle(request);
        response.with(HttpConstants.Fields.REQUEST_ID, OperationContext.getRequestId());
        Optional.ofNullable(response.get(HttpConstants.Fields.STATUS))
                .ifPresent(status -> servletResponse.setStatus((Integer) status));
        return response;
    }
}

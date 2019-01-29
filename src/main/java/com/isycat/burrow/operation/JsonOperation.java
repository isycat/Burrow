package com.isycat.burrow.operation;

import com.isycat.burrow.HttpConstants;
import com.isycat.burrow.json.JsonRequest;
import com.isycat.burrow.json.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class JsonOperation<RequestType extends JsonRequest, ResponseType extends JsonResponse>
        extends OperationHandler<RequestType, ResponseType> {
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

    @Override
    public final ResponseType getResponse(final RequestType request,
                                    final HttpServletRequest servletRequest,
                                    final HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType(APPLICATION_JSON_CONTENT_TYPE);
        final ResponseType response = this.handle(request);
        response.with(HttpConstants.Fields.REQUEST_ID, OperationContext.getRequestId());
        Optional.of(response.getStatus())
                .ifPresent(servletResponse::setStatus);
        return response;
    }
}

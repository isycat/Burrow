package com.isycat.servlet.json;

import com.google.gson.JsonPrimitive;
import com.isycat.servlet.HttpConstants;
import com.isycat.servlet.ServletActivity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class JsonActivity<RequestType extends JsonRequest, ResponseType extends JsonResponse>
        extends ServletActivity<RequestType, ResponseType> {
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    public static final JsonException SERVER_INTERNAL =
            new JsonException(500, "An internal error occurred.");

    @Override
    public final Object getResponse(final String requestId,
                                          final RequestType request,
                                          final HttpServletRequest servletRequest,
                                          final HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType(APPLICATION_JSON_CONTENT_TYPE);
        final JsonResponse jsonResponse = this.handle(request)
                .with(HttpConstants.Fields.REQUEST_ID, new JsonPrimitive(requestId));
        Optional.ofNullable(jsonResponse.get(HttpConstants.Fields.STATUS))
                .ifPresent(status -> servletResponse.setStatus((Integer) status));
        return jsonResponse;
    }
}

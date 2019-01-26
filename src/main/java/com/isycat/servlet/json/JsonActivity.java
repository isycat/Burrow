package com.isycat.servlet.json;

import com.google.gson.JsonPrimitive;
import com.isycat.servlet.ServletActivity;
import com.isycat.servlet.ServletRouter.Fields;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class JsonActivity extends ServletActivity {
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    // todo: Proper DTO (& import babybelle gson wrapper)
    private static final JsonException SERVER_INTERNAL =
            new JsonException(500, "An internal error occurred.");

    @Override
    public final void handleRequest(final JsonRequest request,
                                    final HttpServletResponse response,
                                    final String requestId) throws IOException {
        final JsonResponse jsonResponse = handleResponse(request)
                .with(Fields.REQUEST_ID, new JsonPrimitive(requestId));
        response.setStatus((Integer) jsonResponse.get(Fields.STATUS));
        response.setContentType(APPLICATION_JSON_CONTENT_TYPE);
        response.getWriter().println(jsonResponse);
    }

    private JsonResponse handleResponse(final JsonRequest request) {
        try {
            return this.handle(request);
        } catch (final Exception e) {
            // todo: proper logging
            e.printStackTrace();
            return SERVER_INTERNAL;
        }
    }

    /**
     * Override to implement your operation's functionality.
     *
     * @param request the request to be handled
     * @return JsonResponse object to be returned to user
     * @throws IOException usually on writing response
     */
    public abstract JsonResponse handle(final JsonRequest request) throws IOException;
}

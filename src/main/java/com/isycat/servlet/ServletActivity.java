package com.isycat.servlet;

import com.isycat.servlet.json.JsonActivity;
import com.isycat.servlet.json.JsonException;
import com.isycat.servlet.json.JsonRequest;
import com.isycat.servlet.json.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class ServletActivity<RequestType extends JsonRequest, ResponseType> {
    public static ServletActivity NONE = new JsonActivity<JsonRequest, JsonResponse>() {
        @Override
        public JsonResponse handle(final JsonRequest request) {
            return new JsonException(404, "Unknown Operation");
        }
    };

    public void handleRequest(final String requestId,
                              final Map<String, String> pathFields,
                              final HttpServletRequest servletRequest,
                              final HttpServletResponse servletResponse)
            throws IOException, InstantiationException, IllegalAccessException {
        final RequestType typedRequest = processRequest(pathFields, servletRequest);
        writeResponse(
                this.getResponse(
                        requestId,
                        typedRequest,
                        servletRequest,
                        servletResponse),
                servletResponse);
    }

    protected void writeResponse(final Object response,
                                 final HttpServletResponse servletResponse) throws IOException {
        servletResponse.getWriter().println(response);
    }

    private RequestType processRequest(final Map<String, String> pathFields,
                                       final HttpServletRequest request)
            throws IllegalAccessException, InstantiationException {
        final RequestType typedRequest = createTypedRequest();
        pathFields.forEach(typedRequest::put);
        final Map<String, String> headers = new HashMap<>();
        final Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement().toString();
            headers.put(headerName, request.getHeader(headerName));
        }
        typedRequest.with("headers", headers);
        return typedRequest;
    }

    private RequestType createTypedRequest() throws IllegalAccessException, InstantiationException {
        // This genuinely is necessary to provide generic type passthrough >_<.
        final ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        final Class<RequestType> requestClass = (Class<RequestType>) parameterizedType.getActualTypeArguments()[0];
        return requestClass.newInstance();
    }

    protected abstract Object getResponse(final String requestId,
                                          final RequestType request,
                                          final HttpServletRequest servletRequest,
                                          final HttpServletResponse servletResponse) throws IOException;

    /**
     * Override to implement your operation's functionality.
     *
     * @param request the request to be handled
     * @return Generic ResponseType object to be returned to user
     * @throws IOException usually on writing response
     */
    public abstract ResponseType handle(final RequestType request) throws IOException;
}

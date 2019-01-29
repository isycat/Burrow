package com.isycat.burrow.operation;

import com.google.gson.internal.LinkedTreeMap;
import com.isycat.burrow.ErrorHandler;
import com.isycat.burrow.Logger;
import com.isycat.burrow.error.OperationError;
import com.isycat.burrow.error.UnknownOperationError;
import com.isycat.burrow.json.JsonRequest;
import com.isycat.burrow.json.JsonResponse;
import com.isycat.burrow.serialization.StrictSerializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public abstract class OperationHandler<RequestType extends JsonRequest, ResponseType>
        implements ErrorHandler {
    public static OperationHandler NONE = new JsonOperation<JsonRequest, JsonResponse>() {
        @Override
        public JsonResponse handle(final JsonRequest request) {
            throw new UnknownOperationError();
        }
    };

    public final void handleRequest(final HttpServletRequest servletRequest,
                                    final HttpServletResponse servletResponse)
            throws Exception {
        writeResponse(
                this.getResponse(
                        createTypedRequest(servletRequest),
                        servletRequest,
                        servletResponse),
                servletResponse);
    }

    @Override
    public void handleError(final OperationError operationError,
                                  final HttpServletResponse response) throws Exception {
        ErrorHandler.DEFAULT.handleError(operationError, response);
    }

    protected void writeResponse(final ResponseType response,
                                 final HttpServletResponse servletResponse) throws Exception {
        servletResponse.getWriter().println(response);
    }

    private RequestType createTypedRequest(final HttpServletRequest request) {
        final Map<String, Object> requestMap = new LinkedTreeMap<>();
        requestMap.putAll(OperationContext.getPathFields().orElse(new HashMap<>()));
        requestMap.put("headers", getRequestHeaders(request));
        final RequestType typedRequest = StrictSerializer.deserialize(
                StrictSerializer.serialize(requestMap), getRequestClass());
        Logger.info("Request generated: " + typedRequest);
        return typedRequest;
    }

    private Map<String, String> getRequestHeaders(final HttpServletRequest request) {
        final Map<String, String> headers = new HashMap<>();
        final Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement().toString();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private Class<RequestType> getRequestClass() {
        // This genuinely is necessary to provide generic type passthrough >_<.
        final ParameterizedType parameterizedType =
                (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<RequestType>) parameterizedType.getActualTypeArguments()[0];
    }

    protected abstract ResponseType getResponse(final RequestType request,
                                                final HttpServletRequest servletRequest,
                                                final HttpServletResponse servletResponse) throws Exception;

    /**
     * Override to implement your operation's functionality.
     *
     * @param request the request to be handled
     * @return Generic ResponseType object to be returned to user
     * @throws IOException usually on writing response
     */
    public abstract ResponseType handle(final RequestType request) throws OperationError;
}

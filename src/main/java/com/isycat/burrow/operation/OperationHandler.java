package com.isycat.burrow.operation;

import com.google.gson.internal.LinkedTreeMap;
import com.isycat.burrow.ErrorHandler;
import com.isycat.burrow.error.OperationError;
import com.isycat.burrow.error.ServerInternalError;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class OperationHandler<RequestType extends JsonRequest, ResponseType>
        implements ErrorHandler {
    public static OperationHandler NONE = new JsonOperation<JsonRequest, JsonResponse>() {
        @Override
        public JsonResponse handle(final JsonRequest request) {
            throw new UnknownOperationError();
        }
    };

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<String, String> headers = new HashMap<>();

    public final void handleRequest(final HttpServletRequest servletRequest,
                                    final HttpServletResponse servletResponse)
            throws Exception {
        headers.forEach(servletResponse::setHeader);
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

    public <T> Supplier<T> runParallel(final Callable<T> task) {
        return runParallel(task, executor);
    }

    public Future runParallel(final Runnable task) {
        return executor.submit(task);
    }

    private RequestType createTypedRequest(final HttpServletRequest request) {
        final Map<String, Object> requestMap = new LinkedTreeMap<>();
        requestMap.putAll(OperationContext.getPathFields().orElse(new HashMap<>()));
        requestMap.put("headers", getRequestHeaders(request));
        try {
            if (request.getInputStream() != null) {
                final String dataString = new String(
                        request.getInputStream().readAllBytes(),
                        UTF_8);
                final Map data = StrictSerializer.deserialize(dataString, LinkedHashMap.class);
                data.forEach((k, v) -> requestMap.put((String) k, v));
            }
        } catch (final IOException e) {
            // there is no request data
        }
        return StrictSerializer.deserialize(
                StrictSerializer.serialize(requestMap), getRequestClass());
    }

    protected void setResponseHeader(final String header, final String value) {
        this.headers.put(header, value);
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

    public static <T> Supplier<T> runParallel(final Callable<T> task, final ExecutorService executor) {
        final Future<T> future = executor.submit(task);
        return () -> {
            try {
                return future.get();
            } catch (final Exception e) {
                throw new ServerInternalError(e);
            }
        };
    }
}

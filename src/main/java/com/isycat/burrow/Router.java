package com.isycat.burrow;

import com.isycat.burrow.HttpConstants.Headers;
import com.isycat.burrow.error.OperationError;
import com.isycat.burrow.operation.AbstractServletOperationHandler;
import com.isycat.burrow.operation.OperationContext;
import com.isycat.burrow.operation.OperationRoute;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.isycat.burrow.operation.JsonOperation.SERVER_INTERNAL;

public abstract class Router extends HttpServlet {
    private final OperationRoute[] operationRoutes;

    /**
     * Constructor.
     *
     * @param operationRoutes priority ordered array of path routes to operation handlers.
     */
    public Router(final OperationRoute... operationRoutes) {
        super();
        this.operationRoutes = operationRoutes;
    }

    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        // Force request ID generation
        OperationContext.getRequestId();
        try {
            try {
                handleRequest(request, response);
            } catch (final OperationError operationError) {
                handleErrorInternal(operationError, response);
            } catch (final Exception e) {
                Logger.error("Handling unchecked error", e);
                handleErrorInternal(SERVER_INTERNAL, response);
            }
        } catch (final Exception e) {
            Logger.error("End of the line. Unhandled exception.", e);
        }
        // reset in case of bad thread reuse
        OperationContext.reset();
    }

    private void handleErrorInternal(final OperationError operationError,
                                     final HttpServletResponse response) throws Exception {
        response.setStatus(operationError.getStatus());
        response.addHeader(Headers.REQUEST_ID, OperationContext.getRequestId());
        response.addDateHeader(Headers.DATE, new Date().getTime());
        handleError(operationError, response);
    }

    /**
     * Controls how errors are converted into responses.
     *
     * @param operationError the error to be handled
     * @param response the servlet response
     * @throws Exception pokémon
     */
    protected void handleError(final OperationError operationError,
                               final HttpServletResponse response) throws Exception {
        response.getWriter().println(operationError);
    }

    private void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final AbstractServletOperationHandler operationHandler =
                routeOperation(request.getRequestURI());
        operationHandler.handleRequest(request, response);
        response.addHeader(Headers.REQUEST_ID, OperationContext.getRequestId());
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private AbstractServletOperationHandler routeOperation(final String path) {
        final Optional<OperationRoute> operationRoute = findRoute(path);
        final Map<String, String> pathFields = operationRoute
                .map(route -> route.getPathFields(path))
                .orElse(Collections.emptyMap());
        OperationContext.setPathFields(pathFields);
        final AbstractServletOperationHandler operationHandler = operationRoute
                .map(OperationRoute::getNewOperationHandler)
                .orElse(AbstractServletOperationHandler.NONE);
        Logger.info(operationHandler == AbstractServletOperationHandler.NONE
                ? "No operation handler for path " + path
                : "Operation handler: " + operationHandler.getClass().getName());
        return operationHandler;
    }

    private Optional<OperationRoute> findRoute(@Nullable final String path) {
        return Arrays.stream(operationRoutes)
                .filter(operationRoute -> operationRoute.matches(path))
                .findFirst();
    }
}

package com.isycat.burrow;

import com.isycat.burrow.operation.OperationHandler;
import com.isycat.burrow.operation.OperationContext;
import com.isycat.burrow.operation.OperationRoute;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public abstract class Router extends HttpServlet {
    private final BaseRequestHandler requestHandler = new BaseRequestHandler();
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

    /**
     * Entry point for requests.
     *
     * @param request the {@link HttpServletRequest} to be handled
     * @param response the {@link HttpServletResponse} response object to be modified
     */
    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        // reset in case of bad thread reuse
        OperationContext.reset();
        // Force request ID generation
        OperationContext.getRequestId();
        requestHandler.handleRequestSafe(
                () -> routeOperation(request.getRequestURI()),
                request,
                response);
    }

    private OperationHandler routeOperation(final String path) {
        final Optional<OperationRoute> operationRoute = findRoute(path);
        operationRoute.map(route -> route.getPathFields(path))
                .ifPresent(OperationContext::setPathFields);
        final OperationHandler operationHandler = operationRoute
                .map(OperationRoute::getNewOperationHandler)
                .orElse(OperationHandler.NONE);
        Logger.info(operationHandler == OperationHandler.NONE
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

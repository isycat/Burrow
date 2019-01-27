package com.isycat.burrow;

import com.isycat.burrow.HttpConstants.Headers;
import com.isycat.burrow.operation.AbstractServletOperationHandler;
import com.isycat.burrow.operation.OperationRoute;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        try {
            handleRequest(request, response);
        } catch (final Exception e) {
            // todo: proper logging
            System.out.println("Handling unchecked error");
            e.printStackTrace();
            try {
                response.setStatus(500);
                response.getWriter().println(SERVER_INTERNAL);
            } catch (final Exception e2) {
                // todo: proper logging
                // if we get here things are seriously broken
                System.out.println("End of the line.");
                e2.printStackTrace();
            }
        }
    }

    private void handleRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws IllegalAccessException, IOException, InstantiationException {
        final String requestId = UUID.randomUUID().toString().replace("-", "");
        final Optional<OperationRoute> operationRoute = getOperationRoute(request.getRequestURI());
        final AbstractServletOperationHandler operationHandler = operationRoute
                .map(OperationRoute::getNewOperationHandler)
                .orElse(AbstractServletOperationHandler.NONE);
        // todo: proper logging
        System.out.println(operationHandler == AbstractServletOperationHandler.NONE
                ? "[" + requestId + "]" + " No operation handler for path " + request.getRequestURI()
                : "[" + requestId + "]" + " Operation handler: " + operationHandler.getClass().getName());
        final Map<String, String> pathFields = operationRoute
                .map(route -> route.getPathFields(request.getRequestURI()))
                .orElse(Collections.emptyMap());

        operationHandler.handleRequest(requestId, pathFields, request, response);

        response.addHeader(Headers.REQUEST_ID, requestId);
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private Optional<OperationRoute> getOperationRoute(@Nullable final String path) {
        return Arrays.stream(operationRoutes)
                .filter(operationRoute -> operationRoute.matches(path))
                .findFirst();
    }
}

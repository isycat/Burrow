package com.isycat.servlet;

import com.isycat.servlet.json.JsonRequest;
import com.isycat.servlet.HttpConstants.Headers;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public abstract class ServletRouter extends HttpServlet {
    private final ActivityRoute[] activityRoutes;

    /**
     * Constructor.
     *
     * @param activityRoutes priority ordered list of activitySupplier activityRoutes.
     */
    public ServletRouter(final ActivityRoute... activityRoutes) {
        super();
        this.activityRoutes = activityRoutes;
    }

    // todo fix unhandled exceptions
    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String requestId = UUID.randomUUID().toString().replace("-", "");
        // todo: proper logging
        final Optional<ActivityRoute> activityRoute = getActivityRoute(request.getRequestURI());
        final ServletActivity activity = activityRoute
                .map(ActivityRoute::getNewActivity)
                .orElse(ServletActivity.NONE);
        System.out.println(activity == ServletActivity.NONE
                ? "[" + requestId + "]" + " No operation handler for path " + request.getRequestURI()
                : "[" + requestId + "]" + " Operation handler: " + activity.getClass().getName());
        activity.handleRequest(
                processRequest(activityRoute, request),
                response,
                requestId);
        response.addHeader(Headers.REQUEST_ID, requestId);
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private JsonRequest processRequest(final Optional<ActivityRoute> activityRoute,
                                       final HttpServletRequest request) {
        final JsonRequest jsonRequest = new JsonRequest();
        activityRoute.ifPresent(route -> route.getPathFields(request.getRequestURI())
                .forEach(jsonRequest::put));
        final Map<String, String> headers = new HashMap<>();
        final Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement().toString();
            headers.put(headerName, request.getHeader(headerName));
        }
        jsonRequest.with("headers", headers);
        return jsonRequest;
    }

    private Optional<ActivityRoute> getActivityRoute(@Nullable final String path) {
        return Arrays.stream(activityRoutes)
                .filter(activityRoute -> activityRoute.matches(path))
                .findFirst();
    }
}

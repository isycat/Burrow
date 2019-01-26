package com.isycat.servlet;

import com.isycat.servlet.HttpConstants.Headers;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.isycat.servlet.json.JsonActivity.SERVER_INTERNAL;

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

    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            handleRequest(request, response);
        } catch (final Exception e) {
            // todo: proper logging
            System.out.println("Handling unchecked exception");
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
        final Optional<ActivityRoute> activityRoute = getActivityRoute(request.getRequestURI());
        final ServletActivity activity = activityRoute
                .map(ActivityRoute::getNewActivity)
                .orElse(ServletActivity.NONE);
        // todo: proper logging
        System.out.println(activity == ServletActivity.NONE
                ? "[" + requestId + "]" + " No operation handler for path " + request.getRequestURI()
                : "[" + requestId + "]" + " Operation handler: " + activity.getClass().getName());
        final Map<String, String> pathFields = activityRoute
                .map(route -> route.getPathFields(request.getRequestURI()))
                .orElse(Collections.emptyMap());

        activity.handleRequest(requestId, pathFields, request, response);

        response.addHeader(Headers.REQUEST_ID, requestId);
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private Optional<ActivityRoute> getActivityRoute(@Nullable final String path) {
        return Arrays.stream(activityRoutes)
                .filter(activityRoute -> activityRoute.matches(path))
                .findFirst();
    }
}

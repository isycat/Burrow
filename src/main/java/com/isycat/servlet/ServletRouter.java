package com.isycat.servlet;

import com.google.gson.Gson;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public abstract class ServletRouter extends HttpServlet {
    public static class Fields {
        public static final String REQUEST_ID = "requestId";
        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
    }

    public static class Headers {
        public static final String REQUEST_ID = "request-id";
        public static final String DATE = "date";
    }

    private static final Gson GSON = new Gson();

    private final ActivityRoute[] activityRoutes;

    /**
     * Constructor.
     *
     * @param activityRoutes priority ordered list of activity activityRoutes.
     */
    public ServletRouter(final ActivityRoute... activityRoutes) {
        super();
        this.activityRoutes = activityRoutes;
    }

    // todo fix unhandled exceptions
    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String requestId = UUID.randomUUID().toString().replace("-", "");
        // todo: proper logging
        System.out.println("Received request " + requestId + ": " + GSON.toJson(request));
        final ServletActivity activity = getActivity(request.getServletPath());
        System.out.println("Activity chosen: " + activity.getClass().getName());
        activity.handleRequest(request, response, requestId);
        response.addHeader(Headers.REQUEST_ID, requestId);
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private ServletActivity getActivity(@Nullable final String path) {
        return Arrays.stream(activityRoutes)
                .filter(activityRoute -> activityRoute.matches(path))
                .map(ActivityRoute::getActivity)
                .findFirst()
                .orElse(ServletActivity.NONE);
    }

    /**
     * Class to define regex pathMatcher matcher to route to an activity
     */
    public static class ActivityRoute {
        private final String pathMatcher;
        private final ServletActivity activity;

        public ActivityRoute(final String pathMatcher,
                             final ServletActivity activity) {
            this.pathMatcher = pathMatcher;
            this.activity = activity;
        }

        /**
         * @param path the patch to check
         * @return true if given path fits this route
         */
        public boolean matches(final String path) {
            return Optional.ofNullable(path)
                    .map(p -> p.matches(pathMatcher))
                    .orElse(false);
        }

        /**
         * @return the regex path matcher used to check paths
         */
        public String getPathMatcher() {
            return pathMatcher;
        }

        /**
         * @return the {@link ServletActivity} handled by this route
         */
        public ServletActivity getActivity() {
            return activity;
        }
    }
}

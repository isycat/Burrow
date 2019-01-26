package com.isycat.servlet;

import com.google.gson.Gson;
import com.isycat.servlet.json.JsonRequest;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String NAMED_REGEX_GROUP_REPLACEMENT = "(?<$1>.*)";
    private static final String REGEX_BRACKET_GROUP = "\\{([A-Za-z]+)\\}";
    private static final String REGEX_NAMED_GROUP = "\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>";

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
        final String path = request.getRequestURI();
        final Optional<ActivityRoute> activityRoute = getActivityRoute(path);
        final ServletActivity activity = activityRoute.map(ActivityRoute::getNewActivity)
                .orElse(ServletActivity.NONE);
        System.out.println(activity == ServletActivity.NONE
                ? "[" + requestId + "]" + " No operation handler for path " + request.getRequestURI()
                : "[" + requestId + "]" + " Operation handler: " + activity.getClass().getName());
        final JsonRequest jsonRequest = processRequest(activityRoute, request);
        activity.handleRequest(jsonRequest, response, requestId);
        response.addHeader(Headers.REQUEST_ID, requestId);
        response.addDateHeader(Headers.DATE, new Date().getTime());
    }

    private JsonRequest processRequest(final Optional<ActivityRoute> activityRoute,
                                       final HttpServletRequest request) {
        final JsonRequest jsonRequest = new JsonRequest();
        activityRoute.ifPresent(route -> route.getPathFields(request.getRequestURI())
                .forEach(jsonRequest::put));
        final Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames()).forEach(
                x -> headers.put(x.toString(), request.getHeader(x.toString())));
        jsonRequest.with("headers", headers);
        return jsonRequest;
    }

    private Optional<ActivityRoute> getActivityRoute(@Nullable final String path) {
        return Arrays.stream(activityRoutes)
                .filter(activityRoute -> activityRoute.matches(path))
                .findFirst();
    }

    /**
     * Class to define regex pathMatcher matcher to route to an activitySupplier.
     */
    public static class ActivityRoute {
        private final String pathMatcher;
        private final Supplier<ServletActivity> activitySupplier;
        private final Set<String> groups;

        public ActivityRoute(final String pathMatcher,
                             final Supplier<ServletActivity> activitySupplier) {
            this.pathMatcher = pathMatcher.replaceAll(REGEX_BRACKET_GROUP, NAMED_REGEX_GROUP_REPLACEMENT);
            this.activitySupplier = activitySupplier;
            groups = new HashSet<>();
            final Matcher groupMatcher = Pattern.compile(REGEX_NAMED_GROUP)
                    .matcher(this.pathMatcher);
            while (groupMatcher.find()) {
                groups.add(groupMatcher.group(1));
            }
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
         * @param path The path from which to fetch fields
         * @return field name, field value mappings
         */
        public Map<String, String> getPathFields(final String path) {
            final Matcher matcher = Pattern.compile(pathMatcher)
                    .matcher(path);
            final Map<String, String> groupValues = new HashMap<>();
            while (matcher.find()) {
                groups.forEach(groupName -> groupValues.put(groupName, matcher.group(groupName)));
            }
            return groupValues;
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
        public ServletActivity getNewActivity() {
            return activitySupplier.get();
        }
    }
}

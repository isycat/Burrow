package com.isycat.servlet;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to define regex pathMatcher matcher to route to an activitySupplier.
 */
public class ActivityRoute {
    private static final String NAMED_REGEX_GROUP_REPLACEMENT = "(?<$1>.*)";
    private static final String REGEX_BRACKET_GROUP = "\\{([A-Za-z]+)\\}";
    private static final String REGEX_NAMED_GROUP = "\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>";

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

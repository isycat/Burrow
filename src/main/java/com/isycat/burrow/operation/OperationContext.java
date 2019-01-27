package com.isycat.burrow.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.isycat.burrow.HttpConstants.Fields.REQUEST_ID;

public class OperationContext {
    private static final ThreadLocal<Map<String, String>> context = new ThreadLocal<>();

    public static Map<String, String> init() {
        final Map<String, String> newContext = new HashMap<>();
        context.set(newContext);
        return newContext;
    }

    public static String getRequestId() {
        return get(REQUEST_ID)
                .orElseThrow(() -> new RuntimeException("Request ID not yet set"));
    }

    public static void setRequestId(final String requestId) {
        set(REQUEST_ID, requestId);
    }

    private static Optional<String> get(final String key) {
        return Optional.ofNullable(context.get())
                .map(ctx -> ctx.get(key));
    }

    private static void set(final String key, final String value) {
        Optional.ofNullable(context.get())
                .orElse(init())
                .put(key, value);
    }
}

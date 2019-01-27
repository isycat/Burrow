package com.isycat.burrow.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.isycat.burrow.HttpConstants.Fields.REQUEST_ID;

public class OperationContext {
    private static final ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();

    /**
     * Get an ID for the request currently being handled
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return (String) get(REQUEST_ID)
                .orElseGet(() -> {
                    final String requestId = UUID.randomUUID().toString()
                            .replace("-", "");
                    set(REQUEST_ID, requestId);
                    return requestId;
                });
    }

    public static Optional<Map<String, String>> getPathFields() {
        return get("pathFields")
                .map(v -> (Map<String, String>) v);
    }

    public static void setPathFields(final Map<String, String> pathFields) {
        set("pathFields", pathFields);
    }

    /**
     * Used for protection against sequential thread reuse
     */
    public static void reset() {
        context.set(null);
    }

    private static Map<String, Object> init() {
        final Map<String, Object> newContext = new HashMap<>();
        context.set(newContext);
        return newContext;
    }

    private static Optional<Object> get(final String key) {
        return Optional.ofNullable(context.get())
                .map(ctx -> ctx.get(key));
    }

    private static void set(final String key, final Object value) {
        Optional.ofNullable(context.get())
                .orElse(init())
                .put(key, value);
    }
}

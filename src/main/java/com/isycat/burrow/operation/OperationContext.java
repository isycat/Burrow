package com.isycat.burrow.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.isycat.burrow.HttpConstants.Fields.REQUEST_ID;

public class OperationContext {
    private static final ThreadLocal<Map<String, String>> context = new ThreadLocal<>();

    /**
     * Get an ID for the request currently being handled
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return get(REQUEST_ID)
                .orElseGet(() -> {
                    final String requestId = UUID.randomUUID().toString()
                            .replace("-", "");
                    set(REQUEST_ID, requestId);
                    return requestId;
                });
    }

    /**
     * Used for protection against sequential thread reuse
     */
    public static void reset() {
        context.set(null);
    }

    private static Map<String, String> init() {
        final Map<String, String> newContext = new HashMap<>();
        context.set(newContext);
        return newContext;
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

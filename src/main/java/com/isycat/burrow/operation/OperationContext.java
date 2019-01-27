package com.isycat.burrow.operation;

import com.isycat.burrow.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OperationContext {
    private static final ThreadLocal<Map<String, String>> pathFields = new ThreadLocal<>();
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    /**
     * Get an ID for the request currently being handled
     *
     * @return the request ID
     */
    public static String getRequestId() {
        return Optional.ofNullable(requestId.get())
                .orElseGet(() -> {
                    final String newRequestId = UUID.randomUUID().toString()
                            .replace("-", "");
                    requestId.set(newRequestId);
                    return newRequestId;
                });
    }

    /**
     * Get the path variables for the current request
     * @return Map of key -> value path variables
     */
    public static Optional<Map<String, String>> getPathFields() {
        return Optional.ofNullable(pathFields.get());
    }

    public static void setPathFields(final Map<String, String> newPathFields) {
        pathFields.set(newPathFields);
    }

    /**
     * Used for protection against sequential thread reuse
     */
    public static void reset() {
        Logger.info("Resetting operation context");
        pathFields.set(null);
        requestId.set(null);
    }
}

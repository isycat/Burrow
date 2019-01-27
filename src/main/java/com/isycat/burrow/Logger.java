package com.isycat.burrow;

import com.isycat.burrow.operation.OperationContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Stream;

public class Logger {
    private static final java.util.logging.Logger LOG =
            java.util.logging.Logger.getLogger(Logger.class.getName());

    public static void warn(final String message) {
        processMessage(message).forEach(LOG::warning);
    }

    public static void info(final String message) {
        processMessage(message).forEach(LOG::info);
    }

    public static void error(final String message) {
        processMessage(message).forEach(LOG::severe);
    }

    public static void error(final String message, final Exception exception) {
        final StringWriter stackWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackWriter));
        processMessage(message).forEach(LOG::severe);
        processMessage(stackWriter.toString()).forEach(LOG::severe);
    }

    private static Stream<String> processMessage(final String message) {
        return Arrays.stream(message.split("\n"))
                .map(m -> "[" + OperationContext.getRequestId() + "] " + m);
    }
}

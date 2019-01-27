package com.isycat.burrow;

import com.isycat.burrow.error.ClientError;
import com.isycat.burrow.error.OperationError;
import com.isycat.burrow.error.ServerError;
import com.isycat.burrow.error.ServerInternalError;
import com.isycat.burrow.operation.OperationContext;
import com.isycat.burrow.operation.OperationHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class BaseRequestHandler {
    public void handleRequestSafe(final Supplier<OperationHandler> operationHandlerSupplier,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) {
        try {
            final AtomicReference<ErrorHandler> errorHandler = new AtomicReference<>(null);
            try {
                final OperationHandler operationHandler = operationHandlerSupplier.get();
                errorHandler.set(operationHandler);
                handleRequest(
                        operationHandler,
                        request,
                        response);
            } catch (final ClientError clientError) {
                handleErrorInternal(errorHandler.get(), clientError, response);
            } catch (final ServerError serverError) {
                Logger.error("Server error", serverError);
                handleErrorInternal(errorHandler.get(), serverError, response);
            } catch (final Exception e) {
                Logger.error("Converting unchecked error", e);
                handleErrorInternal(errorHandler.get(), new ServerInternalError(e), response);
            }
        } catch (final Exception e) {
            Logger.error("End of the line. Unhandled exception.", e);
        }
    }

    /**
     * This method exists so that when an error handler is given,
     *   status, requestId, and date headers are still returned.
     *
     * @param errorHandler the {@link ErrorHandler}
     * @param operationError error to be returned
     * @param response the {@link HttpServletResponse} object for the request
     * @throws Exception pokémon
     */
    private void handleErrorInternal(@Nullable final ErrorHandler errorHandler,
                                     @Nonnull final OperationError operationError,
                                     @Nonnull final HttpServletResponse response) throws Exception {
        response.setStatus(operationError.getStatus());
        response.addHeader(HttpConstants.Headers.REQUEST_ID, OperationContext.getRequestId());
        response.addDateHeader(HttpConstants.Headers.DATE, new Date().getTime());
        if (Optional.ofNullable(errorHandler).isPresent()) {
            errorHandler.handleError(operationError, response);
        }
    }

    private void handleRequest(final OperationHandler operationHandler,
                               final HttpServletRequest request,
                               final HttpServletResponse response) throws Exception {
        operationHandler.handleRequest(request, response);
        response.addHeader(HttpConstants.Headers.REQUEST_ID, OperationContext.getRequestId());
        response.addDateHeader(HttpConstants.Headers.DATE, new Date().getTime());
    }
}

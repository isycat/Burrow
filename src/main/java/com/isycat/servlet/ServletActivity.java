package com.isycat.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ServletActivity {
    public static ServletActivity NONE = new ServletActivity() {
        @Override
        public void handleRequest(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final String requestId) throws IOException {
            response.setStatus(500);
            response.getWriter().println("An internal error has occurred");
        }
    };

    public abstract void handleRequest(final HttpServletRequest request,
                                       final HttpServletResponse response,
                                       final String requestId) throws IOException;
}

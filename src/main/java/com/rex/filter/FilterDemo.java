package com.rex.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterDemo implements Filter {

    private static final Logger accessLogger = LogManager.getLogger("AccessLogger");

    @Override
    public void init(FilterConfig filterConfig) {
        accessLogger.info("FilterDemo initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            try {
                ThreadContext.put("ip", httpRequest.getRemoteAddr());
                ThreadContext.put("method", httpRequest.getMethod());
                ThreadContext.put("uri", httpRequest.getRequestURI());

                ThreadContext.put("tenantId", "rex-tenant-id");
                ThreadContext.put("userId", "rex-user-id");

                chain.doFilter(request, response);

                ThreadContext.put("status", String.valueOf(httpResponse.getStatus()));

                accessLogger.info("Processing request");

            } finally {
                ThreadContext.clearAll();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        accessLogger.info("FilterDemo destroyed");
    }
}

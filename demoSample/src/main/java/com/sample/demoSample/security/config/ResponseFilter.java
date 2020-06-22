package com.sample.demoSample.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@WebFilter("/*")
public class ResponseFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseFilter.class);

    @Override
    public void init(FilterConfig config) {
        // NOOP.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String path = ((HttpServletRequest) request).getRequestURI();
            if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
            LOGGER.info("Path: " + path);
            if (path != null && ((HttpServletResponse) response).getHeader("processed") == null) {
                String requestBody = new String(requestWrapper.getContentAsByteArray());
                if (request != null && "application/json".equalsIgnoreCase(request.getContentType()))
                    LOGGER.info("Request body: {}", requestBody);

                String responseBody = new String(responseWrapper.getContentAsByteArray());
                LOGGER.info("Response body: {}", responseBody);

                String paginatedSortedResponse = responseBody;
                try {
                    // Replace response
                    Collection<String> headerNames = ((HttpServletResponse) response).getHeaderNames();
                    Map<String, String> headers = new HashMap<>();
                    if (headerNames != null) {
                        for (String headerName : headerNames) {
                            headers.put(headerName, ((HttpServletResponse) response).getHeader(headerName));
                        }
                    }
                    int status = ((HttpServletResponse) response).getStatus();
                    String ct = response.getContentType();
                    response.reset();
                    response.resetBuffer();
                    if (headerNames != null) {
                        for (String headerName : headerNames) {
                            LOGGER.info("setHeader('" + headerName + "'): {}", headers.get(headerName));
                            ((HttpServletResponse) response).setHeader(headerName, headers.get(headerName));
                        }
                    }
                    ((HttpServletResponse) response).setHeader("processed", "true");
                    LOGGER.info("setStatus('" + status + "')");
                    ((HttpServletResponse) response).setStatus(status);
                    LOGGER.info("setContentType('" + ct + "')");
                    response.setContentType(ct);
                    LOGGER.info("writing: " + (paginatedSortedResponse.length() > 200 ? paginatedSortedResponse.substring(0, 200) : paginatedSortedResponse));
                    response.getWriter().write(paginatedSortedResponse);
                    response.getWriter().flush();
                    response.flushBuffer();
                } catch (Exception e) {
                    // Do nothing
                    LOGGER.info("ResponseFilter.doFilter(): NOT JSON ARRAY RESPONSE, DOING NOTHING");
                    responseWrapper.copyBodyToResponse();
                }
            } else {
                LOGGER.info("ResponseFilter.doFilter(): SPECIFIC ENDPOINT, DOING NOTHING");
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    @Override
    public void destroy() {
        // NOOP.
    }
}

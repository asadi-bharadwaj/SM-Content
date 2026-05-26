package com.sm.content.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * **Summary:** HTTP request logging filter.
 * 
 * **Flow:** This filter intercepts every incoming HTTP request, records the start time, 
 * passes the request along the filter chain, and finally logs the request method, URI, 
 * response status, and total processing time in milliseconds.
 * 
 * **Features:** Request monitoring, performance tracking, and audit logging.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

  /**
   * **Summary:** Internal filter logic to log request details.
   * 
   * **Flow:** 
   * 1. Captures the current system time in nanoseconds.
   * 2. Proceeds with the rest of the filter chain via `filterChain.doFilter`.
   * 3. In the `finally` block, calculates the elapsed time.
   * 4. Logs a single line containing method, URI, status code, and duration if INFO logging is enabled.
   * 
   * **Features:** Precise timing of request execution, comprehensive request/response summary.
   * 
   * @param request The incoming HttpServletRequest.
   * @param response The outgoing HttpServletResponse.
   * @param filterChain The filter chain to continue execution.
   * @throws ServletException If a servlet-related error occurs.
   * @throws IOException If an I/O error occurs during filter processing.
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long start = System.nanoTime();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long ms = (System.nanoTime() - start) / 1_000_000L;
      if (log.isInfoEnabled()) {
        log.info(
            "{} {} -> {} ({} ms) [AuthId={}]",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            ms,
            request.getHeader("X-Authenticated-User-Id"));
      }
    }
  }
}

package com.amido.stacks.core.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

  private final String correlationIdHeader;
  private final String mdcTokenKey;

  public CorrelationIdFilter(String correlationIdHeader, String mdcTokenKey) {
    this.correlationIdHeader = correlationIdHeader;
    this.mdcTokenKey = mdcTokenKey;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {
      final String correlationId = UUID.randomUUID().toString();
      MDC.put(mdcTokenKey, correlationId);

      if (!StringUtils.isEmpty(correlationIdHeader)) {
        httpServletResponse.addHeader(correlationIdHeader, correlationId);
      }

      httpServletRequest.setAttribute(mdcTokenKey, correlationId);
      filterChain.doFilter(httpServletRequest, httpServletResponse);

    } finally {
      MDC.remove(mdcTokenKey);
    }
  }
}

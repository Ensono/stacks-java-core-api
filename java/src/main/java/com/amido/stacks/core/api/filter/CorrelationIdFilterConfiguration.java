package com.amido.stacks.core.api.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrelationIdFilterConfiguration {

  public static final String DEFAULT_CORRELATION_ID_HEADER = "x-correlation-id";
  public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "CorrelationId";

  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> servletRegistrationBean() {

    final FilterRegistrationBean<CorrelationIdFilter> registrationBean =
        new FilterRegistrationBean<>();

    final CorrelationIdFilter logFilter =
        new CorrelationIdFilter(DEFAULT_CORRELATION_ID_HEADER, DEFAULT_MDC_UUID_TOKEN_KEY);

    registrationBean.setFilter(logFilter);
    return registrationBean;
  }
}

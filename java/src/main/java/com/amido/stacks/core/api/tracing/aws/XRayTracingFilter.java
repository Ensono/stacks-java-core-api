package com.amido.stacks.core.api.tracing.aws;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.log4j.Log4JSegmentListener;
import com.amazonaws.xray.metrics.MetricsSegmentListener;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.EKSPlugin;
import javax.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "aws.xray.enabled", havingValue = "true")
@Slf4j
public class XRayTracingFilter {

  @Value("${spring.application.name:undefined}")
  public String appName;

  @Bean
  public Filter xrayFilter() {

    log.info("Initialising AWS XRay Support...");

    AWSXRayRecorderBuilder builder =
        AWSXRayRecorderBuilder.standard()
            .withPlugin(new EC2Plugin())
            .withPlugin(new EKSPlugin())
            .withSegmentListener(new MetricsSegmentListener())
            .withSegmentListener(new Log4JSegmentListener(appName));

    AWSXRay.setGlobalRecorder(builder.build());

    return new AWSXRayServletFilter(appName);
  }
}

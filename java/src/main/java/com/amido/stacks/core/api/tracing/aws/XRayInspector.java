package com.amido.stacks.core.api.tracing.aws;

import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(value = "aws.xray.enabled", havingValue = "true")
public class XRayInspector extends BaseAbstractXRayInterceptor {
  @Override
  @Pointcut("within(com.amido.stacks..*) && bean(*Controller)")
  public void xrayEnabledClasses() {
    // Pointcut
  }
}

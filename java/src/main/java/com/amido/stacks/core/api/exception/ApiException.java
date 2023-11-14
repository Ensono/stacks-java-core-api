package com.amido.stacks.core.api.exception;

public abstract class ApiException extends RuntimeException {

  final int operationCode;
  final String correlationId;

  protected ApiException(String message, int operationCode, String correlationId) {
    super(message);
    this.operationCode = operationCode;
    this.correlationId = correlationId;
  }

  public int getOperationCode() {
    return operationCode;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public abstract int getExceptionCode();
}

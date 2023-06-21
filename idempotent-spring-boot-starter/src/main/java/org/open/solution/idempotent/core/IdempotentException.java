package org.open.solution.idempotent.core;

/**
 * 幂等异常
 */
public class IdempotentException extends RuntimeException {

  public IdempotentException(String errorMessage) {
    super(errorMessage);
  }
}

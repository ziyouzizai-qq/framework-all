package org.open.solution.idempotent.ex;

/**
 * 幂等异常
 */
public class IdempotentException extends RuntimeException {

  public IdempotentException(String errorMessage) {
    super(errorMessage);
  }
}

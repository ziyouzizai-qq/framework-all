package org.open.solution.idempotent.ex;

/**
 * token不存在异常
 */
public class TokenNotFoundException extends IdempotentConfigException {

  public TokenNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}

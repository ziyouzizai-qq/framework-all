package org.open.solution.idempotent.core.type.token;

import org.open.solution.idempotent.core.IdempotentConfigException;

/**
 * token不存在异常
 */
public class TokenNotFoundException extends IdempotentConfigException {

  public TokenNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}

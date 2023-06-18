package org.open.solution.idempotent.core;

/**
 * 幂等配置相关异常
 */
public class IdempotentConfigException extends RuntimeException {

    public IdempotentConfigException(String errorMessage) {
        super(errorMessage);
    }
}

package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;

/**
 * 幂等异常
 */
@RequiredArgsConstructor
public class IdempotentException extends RuntimeException {

    public final String errorMessage;
}

package org.open.solution.idempotent.core.scene.state;

import org.open.solution.idempotent.core.IdempotentConfigException;

/**
 * state 监听超时异常
 */
public class StateMonitorTimeOutException extends IdempotentConfigException {

    public StateMonitorTimeOutException(String errorMessage) {
        super(errorMessage);
    }
}

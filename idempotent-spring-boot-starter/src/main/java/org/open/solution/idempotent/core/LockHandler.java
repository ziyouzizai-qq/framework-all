package org.open.solution.idempotent.core;

public interface LockHandler {

    /**
     * 幂等处理逻辑
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParamWrapper wrapper);


    /**
     * 后置处理
     */
    void postProcessing();
}

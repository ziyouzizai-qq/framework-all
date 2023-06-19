package org.open.solution.idempotent.core;

import org.open.solution.idempotent.enums.IdempotentSceneEnum;

public interface IdempotentSceneHandler {

    /**
     * 幂等性级别
     * @return
     */
    IdempotentSceneEnum scene();

    /**
     * 幂等性校验
     *
     * @param param 幂等校验参数
     */
    void validateIdempotent(IdempotentValidateParam param);


    /**
     * 后置处理
     */
    void postProcessing();

    /**
     * 业务异常流程处理
     */
    void exceptionProcessing();
}

package org.open.solution.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.open.solution.idempotent.annotation.Idempotent;

/**
 * 抽象模板处理器IdempotentExecuteHandler
 */
public abstract class AbstractIdempotentTemplate implements IdempotentExecuteHandler {

    /**
     * 构建幂等验证过程中所需要的参数包装器
     *
     * @param idempotentValidateParam AOP 方法处理
     * @return 幂等参数包装器
     */
    protected abstract void buildValidateParam(IdempotentValidateParam idempotentValidateParam);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     * @param idempotentLevelHandler 幂等处理级别
     */
    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent, IdempotentLevelHandler idempotentLevelHandler) {
        // 模板方法模式：构建幂等参数包装器
        IdempotentValidateParam idempotentValidateParam = IdempotentValidateParam.builder()
            .joinPoint(joinPoint)
            .idempotent(idempotent).build();
        buildValidateParam(idempotentValidateParam);
        idempotentLevelHandler.validateIdempotent(idempotentValidateParam);
    }
}

package org.open.solution.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;

/**
 * 幂等执行处理器
 */
public interface IdempotentExecuteHandler {

    /**
     * 幂等性级别
     * @return
     */
    IdempotentTypeEnum type();

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     * @param idempotentLevelHandler 幂等处理级别
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent, IdempotentLevelHandler idempotentLevelHandler);

}

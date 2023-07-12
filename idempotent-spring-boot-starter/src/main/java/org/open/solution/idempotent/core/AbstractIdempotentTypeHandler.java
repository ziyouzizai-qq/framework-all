package org.open.solution.idempotent.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.toolkit.LogUtil;

/**
 * AbstractIdempotentTypeHandler
 */
public abstract class AbstractIdempotentTypeHandler implements IdempotentExecuteHandler {

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
   * @param joinPoint              AOP 方法处理
   * @param idempotent             幂等注解
   * @param idempotentLevelHandler 幂等处理级别
   */
  public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent,
                      IdempotentSceneHandler idempotentLevelHandler) {
    // 模板方法模式：构建幂等参数包装器
    IdempotentValidateParam idempotentValidateParam = IdempotentValidateParam.builder()
        .joinPoint(joinPoint)
        .logger(LogUtil.getLog(joinPoint))
        .idempotent(idempotent).build();
    buildValidateParam(idempotentValidateParam);
    idempotentLevelHandler.validateIdempotent(idempotentValidateParam);
  }
}

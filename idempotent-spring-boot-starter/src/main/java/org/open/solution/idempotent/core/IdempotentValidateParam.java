package org.open.solution.idempotent.core;

import lombok.Builder;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.open.solution.idempotent.annotation.Idempotent;
import org.slf4j.Logger;

/**
 * 幂等参数包装
 */
@Data
@Builder
public class IdempotentValidateParam {

  /**
   * 注解
   */
  private Idempotent idempotent;

  /**
   * 切点
   */
  private ProceedingJoinPoint joinPoint;

  /**
   * 锁标识
   */
  private String lockKey;

  /**
   * 日志
   */
  private Logger logger;

}

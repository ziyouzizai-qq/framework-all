package org.open.solution.distributed.lock.core;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import org.open.solution.distributed.lock.toolkit.SpELParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * DistributedLockAspect class
 *
 * @author nj
 * @date 2024/2/26
 **/
@Aspect
@RequiredArgsConstructor
public class DistributedLockAspect {

  private final DistributedLockFactory distributedLockFactory;

  private final SpELParser spELParser;

  /**
   * 增强方法标记 {@link org.open.solution.distributed.lock.annotation.DistributedLock} 注解逻辑
   */
  @Around("distributedLockMethods()")
  public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
    org.open.solution.distributed.lock.annotation.DistributedLock distributedLock = getDistributedLock(joinPoint);
    String key = String.format("%s%s%s", distributedLock.prefixKey(),
            spELParser.parse(distributedLock.lockKey(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs()), distributedLock.suffixKey());
    DistributedLock lock = distributedLockFactory.getLock(key);
    try {
      lock.lock();
      return joinPoint.proceed();
    } catch (Exception e) {
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Logger logger =  LoggerFactory.getLogger(methodSignature.getDeclaringType());
      logger.error("{}() method, distributed lock name [{}] exception occurred, error message：{}",
          joinPoint.getSignature().getName(),
          lock.getLockName(),
          e.getMessage());
      throw e;
    } finally {
      lock.unlock();
    }
  }

  @Pointcut("@annotation(org.open.solution.distributed.lock.annotation.DistributedLock)")
  public void distributedLockMethods() {}

  public static org.open.solution.distributed.lock.annotation.DistributedLock getDistributedLock(
      ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = joinPoint.getTarget().getClass()
        .getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
    return AnnotatedElementUtils.getMergedAnnotation(
        targetMethod, org.open.solution.distributed.lock.annotation.DistributedLock.class);
  }
}

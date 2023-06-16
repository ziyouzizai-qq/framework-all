package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.idempotent.annotation.Idempotent;
import java.lang.reflect.Method;

/**
 * IdempotentAspect class
 *
 * @author nj
 * @date 2023/6/15
 **/
@Aspect
@RequiredArgsConstructor
@Slf4j
public class IdempotentAspect {

  private final IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory;

  private final IdempotentLevelHandlerFactory idempotentLevelHandlerFactory;

  /**
   * 增强方法标记 {@link Idempotent} 注解逻辑
   */
  @Around("@annotation(org.open.solution.idempotent.annotation.Idempotent)")
  public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
    Idempotent idempotent = getIdempotent(joinPoint);
    IdempotentExecuteHandler idempotentExecuteHandler = idempotentExecuteHandlerFactory.getInstance(idempotent.type());
    IdempotentLevelHandler idempotentLevelHandler = idempotentLevelHandlerFactory.getInstance(idempotent.level());
    try {
      idempotentExecuteHandler.execute(joinPoint, idempotent, idempotentLevelHandler);
      return joinPoint.proceed();
    } catch (IdempotentException ex) {
      // log
      log.warn(ex.errorMessage);
      throw ex;
    } finally {
      idempotentLevelHandler.postProcessing();
    }
  }

  public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
    return targetMethod.getAnnotation(Idempotent.class);
  }
}

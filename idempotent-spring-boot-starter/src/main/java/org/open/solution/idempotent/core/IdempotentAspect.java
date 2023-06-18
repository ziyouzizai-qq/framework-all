package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.idempotent.annotation.Idempotent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * IdempotentAspect class
 *
 * @author nj
 * @date 2023/6/15
 **/
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

  private final IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory;

  private final IdempotentLevelHandlerFactory idempotentLevelHandlerFactory;

  /**
   * 增强方法标记 {@link Idempotent} 注解逻辑
   */
  @Around("idempotentMethods()")
  public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
    Idempotent idempotent = getIdempotent(joinPoint);
    IdempotentExecuteHandler idempotentExecuteHandler = idempotentExecuteHandlerFactory.getInstance(idempotent.type());
    IdempotentLevelHandler idempotentLevelHandler = idempotentLevelHandlerFactory.getInstance(idempotent.scene());
    try {
      idempotentExecuteHandler.execute(joinPoint, idempotent, idempotentLevelHandler);
      return joinPoint.proceed();
    } catch (IdempotentException e) {
      Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
      logger.error("{}() method, idempotent exception occurred, error message：{}",
              joinPoint.getSignature().getName(),
              e.getMessage());
      throw e;
    } finally {
      idempotentLevelHandler.postProcessing();
    }
  }

  @Pointcut("@annotation(org.open.solution.idempotent.annotation.token.TokenIdempotent) ||" +
          "@annotation(org.open.solution.idempotent.annotation.dcl.DCLParamIdempotent) ||" +
          "@annotation(org.open.solution.idempotent.annotation.dcl.DCLSpELIdempotent)")
  public void idempotentMethods() {}

  public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
    return AnnotatedElementUtils.getMergedAnnotation(targetMethod, Idempotent.class);
  }
}

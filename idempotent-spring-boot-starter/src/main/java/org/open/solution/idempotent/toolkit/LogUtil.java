package org.open.solution.idempotent.toolkit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LogUtil class
 *
 * @author nj
 * @date 2023/6/30
 **/
public class LogUtil {

  /**
   * 获取 Logger
   */
  public static Logger getLog(ProceedingJoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    return LoggerFactory.getLogger(methodSignature.getDeclaringType());
  }
}

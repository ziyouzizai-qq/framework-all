package org.open.solution.idempotent.core.param;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

/**
 * AbstractIdempotentParamService class
 *
 * @author nj
 * @date 2023/6/15
 **/
public interface IdempotentParamService {

  /**
   * @return 获取当前method url
   */
  default String getPath(ProceedingJoinPoint joinPoint) {
    return DigestUtil.md5Hex(((MethodInvocationProceedingJoinPoint) joinPoint).getSignature().toLongString());
  }

  /**
   * @return 当前操作用户 ID
   */
  default String getCurrentUserId() {
    return null;
  }

  /**
   * @return joinPoint md5
   */
  default String calcArgsMD5(ProceedingJoinPoint joinPoint) {
    return DigestUtil.md5Hex(JSONUtil.toJsonStr(joinPoint.getArgs()));
  }
}

package org.open.solution.idempotent.core.param;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * IdempotentParamService class
 *
 * @author nj
 * @date 2023/6/15
 **/
public interface IdempotentParamService {

  /**
   * @return joinPoint md5
   */
  default String calcArgsMD5(ProceedingJoinPoint joinPoint) {
    return DigestUtil.md5Hex(JSONUtil.toJsonStr(joinPoint.getArgs()));
  }
}

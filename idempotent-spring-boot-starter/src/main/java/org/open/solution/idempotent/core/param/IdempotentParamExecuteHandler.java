package org.open.solution.idempotent.core.param;

import org.aspectj.lang.ProceedingJoinPoint;
import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.core.AbstractIdempotentTemplate;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;

/**
 * IdempotentParamExecuteHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public class IdempotentParamExecuteHandler extends AbstractIdempotentTemplate implements IdempotentParamService {

  @Override
  public IdempotentTypeEnum type() {
    return IdempotentTypeEnum.PARAM;
  }

  @Override
  protected void buildValidateParam(ProceedingJoinPoint joinPoint, Idempotent idempotent, IdempotentValidateParam idempotentValidateParam) {
    String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s", getPath(joinPoint), getCurrentUserId(), calcArgsMD5(joinPoint));
    idempotentValidateParam.setLockKey(lockKey);
  }
}
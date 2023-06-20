package org.open.solution.idempotent.core.type.param;

import org.open.solution.idempotent.core.AbstractIdempotentTypeHandler;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;

/**
 * IdempotentParamExecuteHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public class IdempotentParamExecuteHandler extends AbstractIdempotentTypeHandler implements IdempotentParamService {

  @Override
  public IdempotentTypeEnum type() {
    return IdempotentTypeEnum.PARAM;
  }

  @Override
  protected void buildValidateParam(IdempotentValidateParam idempotentValidateParam) {
    String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s",
            getPath(idempotentValidateParam.getJoinPoint()),
            getCurrentUserId(),
            calcArgsMD5(idempotentValidateParam.getJoinPoint()));
    idempotentValidateParam.setLockKey(lockKey);
  }
}

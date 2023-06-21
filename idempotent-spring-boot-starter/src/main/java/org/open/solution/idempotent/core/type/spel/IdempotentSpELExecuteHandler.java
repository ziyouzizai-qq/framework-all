package org.open.solution.idempotent.core.type.spel;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.core.AbstractIdempotentTypeHandler;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.open.solution.idempotent.toolkit.SpELParser;

/**
 * IdempotentSpELExecuteHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
@RequiredArgsConstructor
public class IdempotentSpELExecuteHandler extends AbstractIdempotentTypeHandler implements IdempotentSpELService {

  private final SpELParser spELParser;

  @Override
  public IdempotentTypeEnum type() {
    return IdempotentTypeEnum.SPEL;
  }

  @Override
  protected void buildValidateParam(IdempotentValidateParam idempotentValidateParam) {
    String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s",
        getPath(idempotentValidateParam.getJoinPoint()),
        getCurrentUserId(),
        calcPartArgsMD5(idempotentValidateParam.getJoinPoint(), idempotentValidateParam.getIdempotent()));
    idempotentValidateParam.setLockKey(lockKey);
  }

  private String calcPartArgsMD5(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
    String partKey = (String) spELParser.parse(idempotent.partKey(),
        ((MethodSignature) joinPoint.getSignature()).getMethod(),
        joinPoint.getArgs());

    return DigestUtil.md5Hex(partKey);
  }
}

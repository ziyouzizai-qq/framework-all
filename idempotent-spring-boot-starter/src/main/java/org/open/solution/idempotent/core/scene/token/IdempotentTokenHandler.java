package org.open.solution.idempotent.core.scene.token;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.config.IdempotentTokenProperties;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.ex.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Token幂等处理器
 */
@RequiredArgsConstructor
public class IdempotentTokenHandler extends AbstractIdempotentSceneHandler<IdempotentValidateParam> {

  private final StringRedisTemplate stringRedisTemplate;

  private final IdempotentTokenProperties idempotentTokenProperties;

  @Override
  public IdempotentSceneEnum scene() {
    return IdempotentSceneEnum.TOKEN;
  }

  @Override
  public IdempotentValidateParam putContext(IdempotentValidateParam param) {
    return param;
  }

  @Override
  public void doValidate(IdempotentValidateParam wrapper) {
    Boolean tokenDelFlag = stringRedisTemplate.delete(wrapper.getLockKey());
    if (Objects.nonNull(tokenDelFlag) && !tokenDelFlag) {
      throw new IdempotentException(wrapper.getIdempotent().message());
    }
  }

  @Override
  public void handleExProcessing(IdempotentValidateParam param) {
    if (param.getIdempotent().resetException()) {
      stringRedisTemplate.opsForValue().set(param.getLockKey(), "",
          idempotentTokenProperties.getExpiredTime(),
          TimeUnit.SECONDS);
    }
  }
}

package org.open.solution.idempotent.core.scene.token;

import lombok.Builder;
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
public class IdempotentTokenHandler
    extends AbstractIdempotentSceneHandler<IdempotentTokenHandler.IdempotentTokenWrapper> {

  private final StringRedisTemplate stringRedisTemplate;

  private final IdempotentTokenProperties idempotentTokenProperties;

  @Override
  public IdempotentSceneEnum scene() {
    return IdempotentSceneEnum.TOKEN;
  }

  @Override
  public IdempotentTokenWrapper putContext(IdempotentValidateParam param) {
    return IdempotentTokenWrapper.builder()
        .lockKey(param.getLockKey())
        .resetException(param.getIdempotent().resetException())
        .message(param.getIdempotent().message())
        .build();
  }

  @Override
  public void doValidate(IdempotentTokenWrapper wrapper) {
    Boolean tokenDelFlag = stringRedisTemplate.delete(wrapper.lockKey);
    if (Objects.nonNull(tokenDelFlag) && !tokenDelFlag) {
      throw new IdempotentException(wrapper.message);
    }
  }

  @Override
  public void handleExProcessing(IdempotentTokenWrapper wrapper) {
    if (wrapper.resetException) {
      stringRedisTemplate.opsForValue().set(wrapper.lockKey, "",
          idempotentTokenProperties.getExpiredTime(),
          TimeUnit.SECONDS);
    }
  }

  @Builder
  public static class IdempotentTokenWrapper {

    private String lockKey;

    private boolean resetException;

    private String message;
  }
}

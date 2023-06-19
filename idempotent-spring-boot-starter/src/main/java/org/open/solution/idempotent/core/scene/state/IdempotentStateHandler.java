package org.open.solution.idempotent.core.scene.state;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.config.IdempotentStateProperties;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentStateEnum;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * state幂等处理器
 *
 * @author nj
 * @date 2023/6/19
 **/
@RequiredArgsConstructor
public class IdempotentStateHandler extends AbstractIdempotentSceneHandler {

  private final StringRedisTemplate stringRedisTemplate;

  private final IdempotentStateProperties idempotentStateProperties;

  @Override
  public IdempotentSceneEnum scene() {
    return IdempotentSceneEnum.STATE;
  }

  @Override
  public void validateIdempotent(IdempotentValidateParam param) {
    String lockKey = param.getLockKey();

    // 设置成功说明缓存中还没有值，但是并不能判定该请求是第一次请求，有以下几种情况
    // 1.如果已经被相同请求调用，但是那次请求业务方出现异常，导致触发exceptionProcessing，则会删除缓存，需要业务方排查异常
    // 2.由于缓存过期限制，定时删除或者内存不够触发算法提前删，切记，尽量避免后者情况。否则比预计的提前过期会出现幂等性问题
    // 3.误操作redis导致，比如说时效性未到就删除数据
    Boolean setIfAbsent = stringRedisTemplate.opsForValue()
        .setIfAbsent(lockKey, IdempotentStateEnum.CONSUMING.getCode(), idempotentStateProperties.getExpirationDate(), TimeUnit.SECONDS);


    if (setIfAbsent != null && !setIfAbsent) {
      long start = System.currentTimeMillis();
      while (true) {
        String state = stringRedisTemplate.opsForValue().get(lockKey);
        if (!StringUtils.hasLength(state)) {
          // 业务异常，则退出
          break;
        }

        if (IdempotentStateEnum.CONSUMED.getCode().equals(state)) {
          throw new IdempotentException(param.getIdempotent().message());
        }
        if ((System.currentTimeMillis() - start) >= idempotentStateProperties.getMonitorTime()) {
          throw new StateMonitorTimeOutException("monitoring state timeout.");
        }
      }
    }
    // 成功则放入
//    IdempotentContext.put(WRAPPER, wrapper);
  }

  @Override
  public void postProcessing() {

  }

  @Override
  public void exceptionProcessing() {

  }
}
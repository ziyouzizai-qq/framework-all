package org.open.solution.idempotent.core.scene.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.core.IdempotentContext;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * state幂等处理器
 *
 * @author nj
 * @date 2023/6/19
 **/
@RequiredArgsConstructor
@Slf4j
public class IdempotentStateHandler extends AbstractIdempotentSceneHandler {

  private final StringRedisTemplate stringRedisTemplate;

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
        .setIfAbsent(lockKey, IdempotentStateEnum.CONSUMING.getCode(), param.getIdempotent().expirationDate(), TimeUnit.SECONDS);


    if (setIfAbsent != null && !setIfAbsent) {
      IdempotentContext.put(null);
      String state = stringRedisTemplate.opsForValue().get(lockKey);
      if (IdempotentStateEnum.CONSUMED.getCode().equals(state)) {
        throw new IdempotentException(param.getIdempotent().message());
        // 该状态有两种可能
        // 1. 有另一个线程在消费.
        // 2. 有另一个线程执行业务逻辑前状态变更为CONSUMING后，还未执行业务逻辑，服务挂了，当前状态则一直到缓存过期，在这段期间
        // 后续合法的重试请求而得不到消费，因此要注意这种情况。
      } else if (IdempotentStateEnum.CONSUMING.getCode().equals(state)) {
        log.info("another thread is consuming.");
        throw new IdempotentException(param.getIdempotent().message());
      } else {
        // 业务异常调用exceptionProcessing导致被删
      }
    } else {
      IdempotentContext.put(param);
    }
  }

  @Override
  public void postProcessing() {
    IdempotentValidateParam param = (IdempotentValidateParam) IdempotentContext.removeLast();
    if (param != null) {
      try {
        stringRedisTemplate.opsForValue().set(param.getLockKey(),
            IdempotentStateEnum.CONSUMED.getCode(),
            param.getIdempotent().expirationDate(),
            TimeUnit.SECONDS);
      } catch (Throwable ex) {
        Logger logger = LoggerFactory.getLogger(param.getJoinPoint().getTarget().getClass());
        logger.error("[{}] Failed to set state anti-heavy token.", param.getLockKey());
      }
    }
  }

  @Override
  public void exceptionProcessing() {
    IdempotentValidateParam param = (IdempotentValidateParam) IdempotentContext.get();
    if (param != null) {
      try {
        stringRedisTemplate.delete(param.getLockKey());
      } catch (Throwable ex) {
        Logger logger = LoggerFactory.getLogger(param.getJoinPoint().getTarget().getClass());
        logger.error("[{}] Failed to delete state anti-heavy token.", param.getLockKey());
      }
    }
  }
}

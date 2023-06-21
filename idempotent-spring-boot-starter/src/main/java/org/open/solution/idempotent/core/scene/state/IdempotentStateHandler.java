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

  private final long ERROR = 3;

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
        .setIfAbsent(lockKey, IdempotentStateEnum.CONSUMING.getCode(), param.getIdempotent().consumingExpirationDate(),
            TimeUnit.SECONDS);
    long startTime = System.currentTimeMillis();

    if (setIfAbsent != null && !setIfAbsent) {
      String state = stringRedisTemplate.opsForValue().get(lockKey);

      // state 为null的情况，以下两种情况对consumingExpirationDate的设置合理性要高，才能避免为null
      // 1.上面设置超时运行到此处正好过期，这种情况不可能，consumingExpirationDate为业务逻辑时间要长，而且这几行代码没有耗时，除非redis极慢
      // 2.被其他线程调用exceptionProcessing，也不会，正常情况如果consumingExpirationDate值合理，异常后都是当前线程来调用，然而exceptionProcessing在当前方法后面执行
      // 因此这种情况在正常情况无法出现
      if (IdempotentStateEnum.CONSUMED.getCode().equals(state) ||
          IdempotentStateEnum.CONSUMING.getCode().equals(state)) {
        IdempotentContext.put(null);
        // 该状态有两种可能
        // 1. 有另一个线程在消费.
        // 2. 有另一个线程执行业务逻辑前状态变更为CONSUMING后，还未执行业务逻辑，服务挂了，当前状态则一直到缓存过期，在这段期间
        // 后续合法的重试请求而得不到消费，因此要注意这种情况。
        Logger logger = LoggerFactory.getLogger(param.getJoinPoint().getTarget().getClass());
        logger.error("[{}] another one is currently being consumed or has already been consumed.", param.getLockKey());
        throw new IdempotentException(param.getIdempotent().message());
      } else if (IdempotentStateEnum.CONSUME_ERROR.getCode().equals(state)) {
        Logger logger = LoggerFactory.getLogger(param.getJoinPoint().getTarget().getClass());
        logger.error("[{}] another task consumption exception, waiting for the lock to be released.",
            param.getLockKey());
        while (System.currentTimeMillis() - startTime <= ERROR * 1000) {
          setIfAbsent = stringRedisTemplate.opsForValue()
              .setIfAbsent(lockKey, IdempotentStateEnum.CONSUMING.getCode(),
                  param.getIdempotent().consumingExpirationDate(), TimeUnit.SECONDS);
          if (setIfAbsent != null && setIfAbsent) {
            break;
          }
        }
        if (setIfAbsent != null && setIfAbsent) {
          IdempotentContext.put(param);
        } else {
          IdempotentContext.put(null);
          throw new IdempotentException(param.getIdempotent().message());
        }
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
        String state = stringRedisTemplate.opsForValue().get(param.getLockKey());
        if (IdempotentStateEnum.CONSUMING.getCode().equals(state)) {
          setValToRedis(param.getLockKey(), IdempotentStateEnum.CONSUMED.getCode(),
              param.getIdempotent().consumedExpirationDate());
        } else if (IdempotentStateEnum.CONSUME_ERROR.getCode().equals(state)) {
          stringRedisTemplate.delete(param.getLockKey());
        }
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
        if (param.getIdempotent().enableProCheck()) {
          // 业务系统异常后设置error状态为2s
          setValToRedis(param.getLockKey(), IdempotentStateEnum.CONSUME_ERROR.getCode(), ERROR);
        } else {
          setValToRedis(param.getLockKey(), IdempotentStateEnum.CONSUMED.getCode(),
              param.getIdempotent().consumedExpirationDate());
        }
      } catch (Throwable ex) {
        Logger logger = LoggerFactory.getLogger(param.getJoinPoint().getTarget().getClass());
        logger.error("[{}] Failed to delete state anti-heavy token.", param.getLockKey());
      }
    }
  }

  private void setValToRedis(String key, String val, long timeout) {
    stringRedisTemplate.opsForValue().set(key,
        val,
        timeout,
        TimeUnit.SECONDS);
  }
}

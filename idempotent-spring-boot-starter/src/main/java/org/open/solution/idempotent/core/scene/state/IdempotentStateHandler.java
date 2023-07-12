package org.open.solution.idempotent.core.scene.state;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.ex.IdempotentException;
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
@Slf4j
public class IdempotentStateHandler
    extends AbstractIdempotentSceneHandler<IdempotentStateHandler.IdempotentStateWrapper> {

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public IdempotentSceneEnum scene() {
    return IdempotentSceneEnum.STATE;
  }

  @Override
  public IdempotentStateWrapper putContext(IdempotentValidateParam param) {
    return IdempotentStateWrapper.builder().param(param).build();
  }

  @Override
  public void doValidate(IdempotentStateWrapper wrapper) {
    IdempotentValidateParam param = wrapper.param;
    String lockKey = param.getLockKey();

    // 设置成功说明缓存中还没有值，但是并不能判定该请求是第一次请求，有以下几种情况
    // 1.如果已经被相同请求调用，但是那次请求业务方出现异常，导致触发exceptionProcessing，则会删除缓存，需要业务方排查异常
    // 2.由于缓存过期限制，定时删除或者内存不够触发算法提前删，切记，尽量避免后者情况。否则比预计的提前过期会出现幂等性问题
    // 3.误操作redis导致，比如说时效性未到就删除数据
    Boolean setIfAbsent = stringRedisTemplate.opsForValue()
        .setIfAbsent(lockKey, IdempotentStateEnum.CONSUMING.getCode(), param.getIdempotent().consumingExpirationDate(),
            TimeUnit.SECONDS);


    if (setIfAbsent != null && !setIfAbsent) {
      wrapper.state = IdempotentStateEnum.CONSUMED;
      String state = stringRedisTemplate.opsForValue().get(lockKey);


      if (!StringUtils.hasLength(state)) {
        // state 为null的情况，以下两种情况对consumingExpirationDate的设置合理性要高，才能避免为null
        // 1.上面设置超时运行到此处正好过期，这种情况不可能，consumingExpirationDate为业务逻辑时间要长，而且这几行代码没有耗时，除非redis极慢
        // 2.被其他线程调用exceptionProcessing，也不会，正常情况如果consumingExpirationDate值合理，异常后都是当前线程来调用，然而exceptionProcessing在当前方法后面执行
        // 因此这种情况也很少出现
        wrapper.param.getLogger().warn("[{}] this key suddenly disappeared.", param.getLockKey());
      }

      if (IdempotentStateEnum.CONSUMED.getCode().equals(state)) {
        throw new IdempotentException(param.getIdempotent().message());
      } else if (IdempotentStateEnum.CONSUMING.getCode().equals(state)) {
        // 该状态有两种可能
        // 1. 有另一个线程在消费.
        // 2. 有另一个线程执行业务逻辑前状态变更为CONSUMING后，还未执行业务逻辑，服务挂了，当前状态则一直到缓存过期，在这段期间
        // 后续合法的重试请求而得不到消费，因此要注意这种情况。
        wrapper.param.getLogger().error("[{}] another task is currently being consumed.", param.getLockKey());
        throw new IdempotentException(param.getIdempotent().message());
      }
    } else {
      wrapper.state = IdempotentStateEnum.CONSUMING;
    }
  }

  @Override
  public void handleProcessing(IdempotentStateWrapper wrapper) {
    if (wrapper != null && wrapper.state == IdempotentStateEnum.CONSUMING) {
      try {
        // 只处理非异常状态
        if (!wrapper.param.isExceptionMark()) {
          // 根据validateIdempotent中对于null的情况，基于合理的consumingExpirationDate值，可以得出当state为null时
          // 说明当前线程执行业务逻辑时触发异常被删除，因此为了使得后续合理的重试请求可以得到继续，则保持未消费的状态。
          String state = stringRedisTemplate.opsForValue().get(wrapper.param.getLockKey());

          if (!StringUtils.hasLength(state)) {
            wrapper.param.getLogger().warn("[{}] this key consumption period is too short.", wrapper.param.getLockKey());
          }

          if (IdempotentStateEnum.CONSUMING.getCode().equals(state)) {
            consumed(wrapper.param);
          } else if (IdempotentStateEnum.CONSUMED.getCode().equals(state)) {
            wrapper.param.getLogger().error("[{}] this key already consumed.", wrapper.param.getLockKey());
          }
        }
      } catch (Throwable ex) {
        wrapper.param.getLogger().error("[{}] Failed to set state anti-heavy token.", wrapper.param.getLockKey());
      }
    }
  }

  @Override
  public void handleExProcessing(IdempotentStateWrapper wrapper) {
    if (wrapper != null && wrapper.state == IdempotentStateEnum.CONSUMING) {
      // 设置异常标记
      wrapper.param.setExceptionMark(true);
      try {
        if (wrapper.param.getIdempotent().resetException()) {
          stringRedisTemplate.delete(wrapper.param.getLockKey());
        } else {
          consumed(wrapper.param);
        }
      } catch (Throwable ex) {
        wrapper.param.getLogger().error("[{}] Failed to set state anti-heavy token.", wrapper.param.getLockKey());
      }
    }
  }

  private void consumed(IdempotentValidateParam param) {
    stringRedisTemplate.opsForValue().set(param.getLockKey(),
        IdempotentStateEnum.CONSUMED.getCode(),
        param.getIdempotent().consumedExpirationDate(),
        TimeUnit.SECONDS);
  }

  @Builder
  public static class IdempotentStateWrapper {

    private IdempotentValidateParam param;

    private IdempotentStateEnum state;
  }
}

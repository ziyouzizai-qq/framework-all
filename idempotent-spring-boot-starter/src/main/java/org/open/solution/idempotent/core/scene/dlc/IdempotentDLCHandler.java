package org.open.solution.idempotent.core.scene.dlc;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentStateEnum;
import org.open.solution.idempotent.toolkit.SpELParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * DLC幂等处理器
 */
@RequiredArgsConstructor
public class IdempotentDLCHandler extends AbstractIdempotentSceneHandler<IdempotentDLCHandler.IdempotentDLCWrapper> {

  private final DistributedLockFactory distributedLockFactory;

  private final SpELParser spELParser;

  private final StringRedisTemplate stringRedisTemplate;

  private final static String CONSUMED = "DLC:CONSUMED:";

  @Override
  public IdempotentSceneEnum scene() {
    return IdempotentSceneEnum.DLC;
  }

  @Override
  public IdempotentDLCWrapper putContext(IdempotentValidateParam param) {
    return IdempotentDLCWrapper.builder()
        .param(param)
        .defaultConsumed(!StringUtils.hasLength(param.getIdempotent().validateApi()))
        .build();
  }

  @Override
  public void doValidate(IdempotentDLCWrapper data) {
    IdempotentValidateParam param = data.param;
    if (param.getIdempotent().enableProCheck() && lookupKey(data)) {
      throw new IdempotentException(param.getIdempotent().message());
    }

    DistributedLock lock = distributedLockFactory.getLock(param.getLockKey());
    if (!lock.tryLock()) {
      throw new IdempotentException(param.getIdempotent().message());
    } else {
      // 将分布式锁放入上下文
      data.lock = lock;
      if (lookupKey(data)) {
        throw new IdempotentException(param.getIdempotent().message());
      }
      // 正在消费中...
      data.state = IdempotentStateEnum.CONSUMING;
    }
  }

  @Override
  public void handleProcessing(IdempotentDLCWrapper param) {
    if (param != null && param.lock != null) {
      if (IdempotentStateEnum.CONSUMING == param.state && // 必须是正在消费的线程
          param.defaultConsumed && // 必须是默认的消费规则
          (!param.param.isExceptionMark() || !param.param.getIdempotent().resetException())) {

        // 默认消费模式
        stringRedisTemplate.opsForValue().set(
            consumedKey(param.param.getLockKey()), IdempotentStateEnum.CONSUMED.getCode(),
            param.param.getIdempotent().consumedExpirationDate(),
            TimeUnit.SECONDS);

      }
      param.lock.unlock();
    }
  }

  @Override
  public void handleExProcessing(IdempotentDLCWrapper wrapper) {
    if (wrapper != null) {
      wrapper.param.setExceptionMark(true);
    }
  }

  private Boolean lookupKey(IdempotentDLCWrapper wrapper) {
    if (wrapper.defaultConsumed) {
      // 默认消费规则
      return stringRedisTemplate.hasKey(consumedKey(wrapper.param.getLockKey()));
    } else {
      // 执行业务层校验接口
      return (Boolean) spELParser.parse(wrapper.param.getIdempotent().validateApi(),
          ((MethodSignature) wrapper.param.getJoinPoint().getSignature()).getMethod(),
          wrapper.param.getJoinPoint().getArgs());
    }
  }

  private String consumedKey(String lockKey) {
    return CONSUMED + lockKey;
  }

  @Builder
  public static class IdempotentDLCWrapper {

    private IdempotentValidateParam param;

    private DistributedLock lock;

    private IdempotentStateEnum state;

    /**
     * 是否为默认消费
     */
    private boolean defaultConsumed;
  }
}

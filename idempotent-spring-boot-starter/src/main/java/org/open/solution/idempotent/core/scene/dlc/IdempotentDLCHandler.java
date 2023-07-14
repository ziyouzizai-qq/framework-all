package org.open.solution.idempotent.core.scene.dlc;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.ex.IdempotentException;
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
public final class IdempotentDLCHandler
    extends AbstractIdempotentSceneHandler<IdempotentDLCHandler.IdempotentDLCWrapper> {

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
        .lockKey(param.getLockKey())
        .joinPoint(param.getJoinPoint())
        .enableProCheck(param.getIdempotent().enableProCheck())
        .message(param.getIdempotent().message())
        .resetException(param.getIdempotent().resetException())
        .validateApi(param.getIdempotent().validateApi())
        .defaultConsumed(!StringUtils.hasLength(param.getIdempotent().validateApi()))
        .consumedExpirationDate(param.getIdempotent().consumedExpirationDate())
        .build();
  }

  @Override
  public void doValidate(IdempotentDLCWrapper wrapper) {
    if (wrapper.enableProCheck && lookupKey(wrapper)) {
      throw new IdempotentException(wrapper.message);
    }

    DistributedLock lock = distributedLockFactory.getLock(wrapper.lockKey);
    if (!lock.tryLock()) {
      throw new IdempotentException(wrapper.message);
    } else {
      // 将分布式锁放入上下文
      wrapper.lock = lock;
      if (lookupKey(wrapper)) {
        throw new IdempotentException(wrapper.message);
      }
      // 正在消费中...
      wrapper.state = IdempotentStateEnum.CONSUMING;
    }
  }

  @Override
  public void handleProcessing(IdempotentDLCWrapper wrapper) {
    if (wrapper.lock != null) {
      if (IdempotentStateEnum.CONSUMING == wrapper.state && // 必须是正在消费的线程
          wrapper.defaultConsumed && // 必须是默认的消费规则
          (!wrapper.exceptionMark || !wrapper.resetException)) {

        // 默认消费模式
        stringRedisTemplate.opsForValue().set(
            consumedKey(wrapper.lockKey), IdempotentStateEnum.CONSUMED.getCode(),
            wrapper.consumedExpirationDate,
            TimeUnit.SECONDS);

      }
      wrapper.lock.unlock();
    }
  }

  @Override
  public void handleExProcessing(IdempotentDLCWrapper wrapper) {
    wrapper.exceptionMark = true;
  }

  private Boolean lookupKey(IdempotentDLCWrapper wrapper) {
    if (wrapper.defaultConsumed) {
      // 默认消费规则
      return stringRedisTemplate.hasKey(consumedKey(wrapper.lockKey));
    } else {
      // 执行业务层校验接口
      return (Boolean) spELParser.parse(wrapper.validateApi,
          ((MethodSignature) wrapper.joinPoint.getSignature()).getMethod(),
          wrapper.joinPoint.getArgs());
    }
  }

  private String consumedKey(String lockKey) {
    return CONSUMED + lockKey;
  }

  @Builder
  public static class IdempotentDLCWrapper {

    private DistributedLock lock;

    private IdempotentStateEnum state;

    private boolean exceptionMark;

    private boolean defaultConsumed;

    private boolean enableProCheck;

    private String validateApi;

    private String lockKey;

    private boolean resetException;

    private ProceedingJoinPoint joinPoint;

    private long consumedExpirationDate;

    private String message;
  }
}

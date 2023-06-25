package org.open.solution.idempotent.core.scene.dlc;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.core.IdempotentContext;
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
public class IdempotentDLCHandler extends AbstractIdempotentSceneHandler {

    private final DistributedLockFactory distributedLockFactory;

    private final SpELParser spELParser;

    private final StringRedisTemplate stringRedisTemplate;

    private final static String CONSUMED = ":CONSUMED";

    @Override
    public IdempotentSceneEnum scene() {
        return IdempotentSceneEnum.DLC;
    }

    @Override
    public void validateIdempotent(IdempotentValidateParam param) {
        IdempotentDLCWrapper idempotentDLCWrapper = IdempotentDLCWrapper.builder()
                .param(param)
                .defaultConsumed(!StringUtils.hasLength(param.getIdempotent().validateApi()))
                .build();
        IdempotentContext.put(idempotentDLCWrapper);
        if (param.getIdempotent().enableProCheck() && validateData(idempotentDLCWrapper)) {
            throw new IdempotentException(param.getIdempotent().message());
        }

        DistributedLock lock = distributedLockFactory.getLock(param.getLockKey());
        if (!lock.tryLock()) {
            throw new IdempotentException(param.getIdempotent().message());
        } else {
            // 将分布式锁放入上下文
            idempotentDLCWrapper.lock = lock;
            if (validateData(idempotentDLCWrapper)) {
                throw new IdempotentException(param.getIdempotent().message());
            }
            // 正在消费中...
            idempotentDLCWrapper.state = IdempotentStateEnum.CONSUMING;
        }
    }

    @Override
    public void postProcessing() {
        IdempotentDLCWrapper wrapper = null;
        try {
            wrapper = (IdempotentDLCWrapper) IdempotentContext.removeLast();
        } finally {
            if (wrapper != null && wrapper.lock != null) {
                if (IdempotentStateEnum.CONSUMING == wrapper.state && // 必须是正在消费的线程
                        wrapper.defaultConsumed && // 必须是默认的消费规则
                        (!wrapper.exMark || !wrapper.param.getIdempotent().resetException())) {

                    // 默认消费模式
                    stringRedisTemplate.opsForValue().set(
                            consumedKey(wrapper.param.getLockKey()), IdempotentStateEnum.CONSUMED.getCode(),
                            wrapper.param.getIdempotent().consumedExpirationDate(),
                            TimeUnit.SECONDS);

                }
                wrapper.lock.unlock();
            }
        }
    }

    @Override
    public void exceptionProcessing() {
        IdempotentDLCWrapper wrapper = (IdempotentDLCWrapper) IdempotentContext.get();
        if (wrapper != null) {
            wrapper.exMark = true;
        }
    }

    private Boolean validateData(IdempotentDLCWrapper wrapper) {
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
        return lockKey + CONSUMED;
    }

    @Builder
    private static class IdempotentDLCWrapper {

        private IdempotentValidateParam param;

        private DistributedLock lock;

        private IdempotentStateEnum state;

        /**
         * 业务是否异常标记
         */
        private boolean exMark;

        /**
         * 是否为默认消费
         */
        private boolean defaultConsumed;

    }
}

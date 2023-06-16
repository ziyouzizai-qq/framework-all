package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.toolkit.SpELParser;

/**
 * 区域性幂等处理器
 */
@RequiredArgsConstructor
public class LockBlockHandler extends AbstractIdempotentLevelHandler {

    /**
     * 块级别的只控制释放锁之前的幂等，完全的幂等，需要callValidateApi
     */
    public static final String BLOCK = "BLOCK";

    private final DistributedLockFactory distributedLockFactory;

    private final SpELParser spELParser;

    @Override
    public String level() {
        return BLOCK;
    }

    @Override
    public void validateIdempotent(IdempotentValidateParam param) {
        String lockKey = param.getLockKey();
        DistributedLock lock = distributedLockFactory.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new IdempotentException(param.getIdempotent().message());
        } else {
            Boolean validate = (Boolean) spELParser.parse(param.getIdempotent().validateApi(),
                ((MethodSignature) param.getJoinPoint().getSignature()).getMethod(),
                param.getJoinPoint().getArgs());

            if (!validate) {
                throw new IdempotentException(param.getIdempotent().message());
            }
        }
        IdempotentContext.putLock(lock);
    }

    @Override
    public void postProcessing() {
        DistributedLock lock = null;
        try {
            lock = IdempotentContext.getLock();
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public boolean validateData() {
        return true;
    }
}

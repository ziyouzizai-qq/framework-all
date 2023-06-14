package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;

/**
 * 区域性处理器
 */
@RequiredArgsConstructor
public abstract class AbstractIdempotentSpaceTemplate extends AbstractIdempotentTemplate {

    private final DistributedLockFactory distributedLockFactory;

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String lockKey = wrapper.getLockKey();
        DistributedLock lock = distributedLockFactory.getLock(lockKey);
        if (!lock.tryLock()) {
            return;
        }
//        IdempotentContext.put(LOCK, lock);
    }

    @Override
    public void postProcessing() {
        DistributedLock lock = null;
        try {
//            lock = (DistributedLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}

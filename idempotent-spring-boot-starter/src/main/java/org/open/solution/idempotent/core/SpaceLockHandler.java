package org.open.solution.idempotent.core;

import lombok.RequiredArgsConstructor;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;

/**
 * 区域性处理器
 */
@RequiredArgsConstructor
public class SpaceLockHandler implements LockHandler {

    private final DistributedLockFactory distributedLockFactory;

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String lockKey = wrapper.getLockKey();
        DistributedLock lock = distributedLockFactory.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new IdempotentException(wrapper.getIdempotent().message());
        }

        // 放入上下文
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

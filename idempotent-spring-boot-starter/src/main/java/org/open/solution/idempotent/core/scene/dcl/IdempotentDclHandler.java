package org.open.solution.idempotent.core.scene.dcl;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.AbstractIdempotentLevelHandler;
import org.open.solution.idempotent.core.IdempotentContext;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.toolkit.SpELParser;

/**
 * DCL幂等处理器
 */
@RequiredArgsConstructor
public class IdempotentDclHandler extends AbstractIdempotentLevelHandler {

    private final DistributedLockFactory distributedLockFactory;

    private final SpELParser spELParser;

    @Override
    public IdempotentSceneEnum scene() {
        return IdempotentSceneEnum.DCL;
    }

    @Override
    public void validateIdempotent(IdempotentValidateParam param) {
        if (param.getIdempotent().enableProCheck() && !validateData(param)) {
            IdempotentContext.putLock(null);
            throw new IdempotentException(param.getIdempotent().message());
        }

        DistributedLock lock = distributedLockFactory.getLock(param.getLockKey());
        if (!lock.tryLock()) {
            IdempotentContext.putLock(null);
            throw new IdempotentException(param.getIdempotent().message());
        } else {
            // 将分布式锁放入上下文
            IdempotentContext.putLock(lock);
            if (!validateData(param)) {
                throw new IdempotentException(param.getIdempotent().message());
            }
        }
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


    private Boolean validateData(IdempotentValidateParam param) {
        // 执行业务层校验接口
        Boolean validate = (Boolean) spELParser.parse(param.getIdempotent().validateApi(),
                ((MethodSignature) param.getJoinPoint().getSignature()).getMethod(),
                param.getJoinPoint().getArgs());
        return validate;
    }
}

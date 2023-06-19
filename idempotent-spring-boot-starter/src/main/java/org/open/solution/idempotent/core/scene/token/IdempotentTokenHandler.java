package org.open.solution.idempotent.core.scene.token;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.config.IdempotentTokenProperties;
import org.open.solution.idempotent.core.AbstractIdempotentSceneHandler;
import org.open.solution.idempotent.core.IdempotentContext;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Token幂等处理器
 */
@RequiredArgsConstructor
public class IdempotentTokenHandler extends AbstractIdempotentSceneHandler {

    private final StringRedisTemplate stringRedisTemplate;

    private final IdempotentTokenProperties idempotentTokenProperties;

    @Override
    public IdempotentSceneEnum scene() {
        return IdempotentSceneEnum.TOKEN;
    }

    @Override
    public void validateIdempotent(IdempotentValidateParam param) {
        // 后置异常处理
        IdempotentContext.put(param);

        Boolean tokenDelFlag = stringRedisTemplate.delete(param.getLockKey());
        if (Objects.nonNull(tokenDelFlag) && !tokenDelFlag) {
            throw new IdempotentException(param.getIdempotent().message());
        }

    }

    @Override
    public void postProcessing() {
        IdempotentContext.removeLast();
    }

    @Override
    public void exceptionProcessing() {
        // 将token重新塞回去
        IdempotentValidateParam param = (IdempotentValidateParam) IdempotentContext.get();
        if (param != null && param.getIdempotent().enableProCheck()) {
            stringRedisTemplate.opsForValue().set(param.getLockKey(), "",
                idempotentTokenProperties.getExpiredTime(),
                TimeUnit.SECONDS);
        }
    }
}

package org.open.solution.idempotent.core.scene.token;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.core.AbstractIdempotentLevelHandler;
import org.open.solution.idempotent.core.IdempotentException;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * Token幂等处理器
 */
@RequiredArgsConstructor
public class IdempotentTokenHandler extends AbstractIdempotentLevelHandler {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public IdempotentSceneEnum scene() {
        return IdempotentSceneEnum.TOKEN;
    }

    @Override
    public void validateIdempotent(IdempotentValidateParam param) {
        Boolean tokenDelFlag = stringRedisTemplate.delete(param.getLockKey());
        if (Objects.nonNull(tokenDelFlag) && !tokenDelFlag) {
            throw new IdempotentException(param.getIdempotent().message());
        }
    }
}

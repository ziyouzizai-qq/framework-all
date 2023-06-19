package org.open.solution.idempotent.core.type.token;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.reflect.MethodSignature;
import org.open.solution.idempotent.config.IdempotentTokenProperties;
import org.open.solution.idempotent.core.AbstractIdempotentTemplate;
import org.open.solution.idempotent.core.IdempotentValidateParam;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.open.solution.idempotent.toolkit.SpELParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class IdempotentTokenExecuteHandler extends AbstractIdempotentTemplate implements IdempotentTokenService {

    private static final String TOKEN_KEY = "idempotent-token";

    private static final String TOKEN_PREFIX_KEY = "idempotent:token:";

    private final SpELParser spELParser;

    private final StringRedisTemplate stringRedisTemplate;

    private final IdempotentTokenProperties idempotentTokenProperties;

    @Override
    protected void buildValidateParam(IdempotentValidateParam idempotentValidateParam) {
        String token = (String) spELParser.parse(idempotentValidateParam.getIdempotent().partKey(),
                ((MethodSignature) idempotentValidateParam.getJoinPoint().getSignature()).getMethod(),
                idempotentValidateParam.getJoinPoint().getArgs());
        if (StrUtil.isBlank(token)) {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            token = request.getHeader(TOKEN_KEY);
            if (StrUtil.isBlank(token)) {
                token = request.getParameter(TOKEN_KEY);
                if (StrUtil.isBlank(token)) {
                    throw new TokenNotFoundException("token not found.");
                }
            }
        }
        String lockKey = lockKey(token);
        idempotentValidateParam.setLockKey(lockKey);
    }

    @Override
    public IdempotentTypeEnum type() {
        return IdempotentTypeEnum.TOKEN;
    }

    @Override
    public String createToken() {
        String uuid = UUID.randomUUID().toString();
        String token = lockKey(uuid);
        stringRedisTemplate.opsForValue().set(token, "", idempotentTokenProperties.getExpiredTime(), TimeUnit.SECONDS);
        return uuid;
    }

    public String defaultToken() {
        return "";
    }

    private String lockKey(String uuid) {
        return TOKEN_PREFIX_KEY + uuid;
    }
}

package org.open.solution.idempotent.core.type.token;

/**
 *
 */
public interface IdempotentTokenService {

    /**
     * 创建幂等验证Token
     */
    String createToken();
}

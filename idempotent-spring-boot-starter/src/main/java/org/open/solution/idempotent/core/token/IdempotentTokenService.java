package org.open.solution.idempotent.core.token;

/**
 *
 */
public interface IdempotentTokenService {

    /**
     * 创建幂等验证Token
     */
    String createToken();
}

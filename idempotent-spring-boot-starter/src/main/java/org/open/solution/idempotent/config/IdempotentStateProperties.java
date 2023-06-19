package org.open.solution.idempotent.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LockRedisProperties class
 *
 * @author nj
 * @date 2023/6/13
 **/
@ConfigurationProperties("open.solution.idempotent.state")
@Getter
@Setter
public class IdempotentStateProperties {

    /**
     * 幂等有效期
     */
    private Long expirationDate = 600L;

    private Long monitorTime = 10 * 1000L;

}

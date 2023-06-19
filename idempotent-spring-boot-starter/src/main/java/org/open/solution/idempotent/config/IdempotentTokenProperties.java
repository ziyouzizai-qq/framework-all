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
@ConfigurationProperties("open.solution.idempotent.token")
@Getter
@Setter
public class IdempotentTokenProperties {

    private Long expiredTime = 600L;

}

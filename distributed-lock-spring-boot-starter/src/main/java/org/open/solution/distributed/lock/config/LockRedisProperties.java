package org.open.solution.distributed.lock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LockRedisProperties class
 *
 * @author nj
 * @date 2023/6/13
 **/
@ConfigurationProperties("open.solution.distributed.lock.redis")
@Getter
@Setter
public class LockRedisProperties {

    private String address;

    private String password;

    private int database;

}

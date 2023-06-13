package org.open.solution.distributed.lock.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LockRedisProperties class
 *
 * @author nj
 * @date 2023/6/13
 **/
@ConfigurationProperties("open.solution.distributed.lock.zk")
@Getter
public class LockZooKeeperProperties {

    private boolean enabled = false;

    private String connectString = "localhost:2181";

    private int baseSleepTimeMs = 100;

    private int maxRetries = 3;

}

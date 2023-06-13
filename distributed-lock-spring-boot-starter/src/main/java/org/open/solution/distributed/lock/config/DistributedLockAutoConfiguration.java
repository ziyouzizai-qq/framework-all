package org.open.solution.distributed.lock.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.open.solution.distributed.lock.core.DistributedLockClient;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.distributed.lock.core.redis.DistributedRedissonClient;
import org.open.solution.distributed.lock.core.zookeeper.DistributedCuratorFrameworkClient;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * DistributedLockAutoConfiguration class
 *
 * @author nj
 * @date 2023/6/13
 **/
@Configuration
public class DistributedLockAutoConfiguration {

  @Bean
  public DistributedLockFactory distributedLockFactory(DistributedLockClient distributedLockClient) {
    return new DistributedLockFactory(distributedLockClient);
  }

  @Configuration
  @EnableConfigurationProperties({LockRedisProperties.class})
  @AutoConfigureAfter(RedissonAutoConfiguration.class)
  @ConditionalOnProperty(value = "open.solution.distributed.lock.redis.enabled", havingValue = "true")
  protected static class DistributedLockRedisConfiguration {

    @Bean
    public DistributedLockClient distributedLockClient(RedissonClient redissonClient) {
      return new DistributedRedissonClient(redissonClient);
    }
  }

  @Configuration
  @ConditionalOnProperty(value = "open.solution.distributed.lock.zk.enabled", havingValue = "true")
  @EnableConfigurationProperties({LockZooKeeperProperties.class})
  protected static class DistributedLockZookeeperConfiguration {

    @Bean
    public CuratorFramework client(LockZooKeeperProperties lockZooKeeperProperties) {
      return CuratorFrameworkFactory.newClient(
              lockZooKeeperProperties.getConnectString(),
              new ExponentialBackoffRetry(lockZooKeeperProperties.getBaseSleepTimeMs(),
                      lockZooKeeperProperties.getMaxRetries()));
    }

    @Bean
    public DistributedLockClient distributedLockClient(CuratorFramework client) {
      return new DistributedCuratorFrameworkClient(client);
    }
  }

}

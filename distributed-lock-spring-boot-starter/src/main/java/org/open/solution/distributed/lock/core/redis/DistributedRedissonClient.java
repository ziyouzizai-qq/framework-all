package org.open.solution.distributed.lock.core.redis;

import lombok.RequiredArgsConstructor;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockClient;
import org.redisson.api.RedissonClient;

/**
 * DistributedRedissonClient class
 *
 * @author nj
 * @date 2023/6/13
 **/
@RequiredArgsConstructor
public class DistributedRedissonClient implements DistributedLockClient {

  private final RedissonClient redissonClient;

  @Override
  public DistributedLock getLock(String key) {
    return new DistributedRLock(redissonClient.getLock(key), key);
  }
}

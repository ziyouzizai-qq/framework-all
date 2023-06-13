package org.open.solution.distributed.lock.core;

public interface DistributedLockClient {

  /**
   * 根据key获取分布式锁
   * @param key
   * @return
   */
  DistributedLock getLock(String key);
}

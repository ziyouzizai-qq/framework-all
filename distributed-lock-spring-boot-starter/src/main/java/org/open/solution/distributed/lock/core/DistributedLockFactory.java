package org.open.solution.distributed.lock.core;

import lombok.RequiredArgsConstructor;

/**
 * DistributedLockFactory class
 *
 * @author nj
 * @date 2023/6/13
 **/
@RequiredArgsConstructor
public final class DistributedLockFactory {

  private final DistributedLockClient distributedLockClient;

  public DistributedLock getLock(String key) {
    return distributedLockClient.getLock(key);
  }
}

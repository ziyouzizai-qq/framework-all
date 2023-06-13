package org.open.solution.distributed.lock.core.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockClient;

/**
 * DistributedCuratorFrameworkClient class
 *
 * @author nj
 * @date 2023/6/13
 **/
public class DistributedCuratorFrameworkClient implements DistributedLockClient {

  private final CuratorFramework client;

  public DistributedCuratorFrameworkClient(CuratorFramework client) {
    this.client = client;
    client.start();
  }

  @Override
  public DistributedLock getLock(String key) {
    return new DistributedInterProcessLock(new InterProcessMutex(client, key));
  }
}

package org.open.solution.distributed.lock.core.zookeeper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.open.solution.distributed.lock.core.DistributedLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * DistributedInterProcessMutex class
 *
 * @author nj
 * @date 2023/6/13
 **/
@RequiredArgsConstructor
public class DistributedInterProcessMutex implements DistributedLock {

  private final InterProcessLock lock;

  @SneakyThrows
  @Override
  public void lock() {
    lock.acquire();
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  @SneakyThrows
  @Override
  public boolean tryLock() {
    return tryLock(0, TimeUnit.MILLISECONDS);
  }

  @SneakyThrows
  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return lock.acquire(time, unit);
  }

  @SneakyThrows
  @Override
  public void unlock() {
    lock.release();
  }

  @Override
  public Condition newCondition() {
    throw new UnsupportedOperationException();
  }
}

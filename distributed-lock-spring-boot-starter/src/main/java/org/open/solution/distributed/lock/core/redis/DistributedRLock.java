package org.open.solution.distributed.lock.core.redis;

import lombok.RequiredArgsConstructor;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.redisson.api.RLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * DistributedRLock class
 *
 * @author nj
 * @date 2023/6/13
 **/
@RequiredArgsConstructor
public class DistributedRLock implements DistributedLock {

  private final RLock lock;

  private final String lockName;

  @Override
  public void lock() {
    lock.lock();
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    lock.lockInterruptibly();
  }

  @Override
  public boolean tryLock() {
    return lock.tryLock();
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return lock.tryLock(time, unit);
  }

  @Override
  public void unlock() {
    lock.unlock();
  }

  @Override
  public Condition newCondition() {
    return lock.newCondition();
  }

  @Override
  public String getLockName() {
    return lockName;
  }
}

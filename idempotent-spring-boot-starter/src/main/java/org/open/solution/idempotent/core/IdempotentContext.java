package org.open.solution.idempotent.core;

import org.open.solution.distributed.lock.core.DistributedLock;
import org.springframework.util.CollectionUtils;
import java.util.Deque;
import java.util.LinkedList;

/**
 * IdempotentContext class
 *
 * @author nj
 * @date 2023/6/15
 **/
public class IdempotentContext {

  private static ThreadLocal<Deque<DistributedLock>> CONTEXT = new ThreadLocal<>();

  /**
   * 将分布式锁缓存到当前线程中
   * @param lock
   */
  public static void putLock(DistributedLock lock) {
    Deque<DistributedLock> lockList = CONTEXT.get();
    if (CollectionUtils.isEmpty(lockList)) {
      lockList = new LinkedList<>();
      CONTEXT.set(lockList);
    }
    lockList.add(lock);
  }

  /**
   * 获取分布式锁且从当前线程中移除
   */
  public static DistributedLock getLock() {
    Deque<DistributedLock> lockList = CONTEXT.get();
    if (CollectionUtils.isEmpty(lockList)) {
      CONTEXT.remove();
      return null;
    }
    DistributedLock distributedLock = lockList.removeLast();
    if (CollectionUtils.isEmpty(lockList)) {
      CONTEXT.remove();
    }
    return distributedLock;
  }
}

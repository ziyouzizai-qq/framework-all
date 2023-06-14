package org.open.solution.distributed.test.lock;

import lombok.RequiredArgsConstructor;
import org.open.solution.distributed.lock.core.DistributedLock;
import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LockApp class
 *
 * @author nj
 * @date 2023/6/14
 **/
@RestController
@RequiredArgsConstructor
public class LockApp {

//  private final DistributedLockFactory distributedLockFactory;

  @GetMapping("/lock")
  public String lock() {
//    DistributedLock l1 = distributedLockFactory.getLock("/l1");
//    try {
//      l1.lock();
//      Thread.sleep(10 * 1000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } finally {
//      l1.unlock();
//    }
    return "OK";
  }

}

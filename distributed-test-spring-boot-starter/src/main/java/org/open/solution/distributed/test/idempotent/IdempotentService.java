package org.open.solution.distributed.test.idempotent;

import org.springframework.stereotype.Service;

/**
 * IdempotentService class
 *
 * @author nj
 * @date 2023/6/16
 **/
@Service
public class IdempotentService {

  private volatile boolean f;

  public boolean validateData(UiIdempotent uiIdempotent) {
    boolean result = !f;
    if (result) {
      f = true;
    }
    return result;
  }
}

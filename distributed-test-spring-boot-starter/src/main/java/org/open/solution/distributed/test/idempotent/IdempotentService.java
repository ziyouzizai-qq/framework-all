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

  public boolean validateData(UiIdempotent uiIdempotent) {
    System.out.println(uiIdempotent.getId());
    System.out.println(uiIdempotent.getOther());
    return true;
  }
}

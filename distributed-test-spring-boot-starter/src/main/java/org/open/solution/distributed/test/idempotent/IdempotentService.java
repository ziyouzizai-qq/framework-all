package org.open.solution.distributed.test.idempotent;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * IdempotentService class
 *
 * @author nj
 * @date 2023/6/16
 **/
@Service
public class IdempotentService {

  private Map<String, UiIdempotent> map = new HashMap<>();

  public boolean validateData(UiIdempotent uiIdempotent) {
    return map.containsKey(uiIdempotent.getId());
  }

  public void add(UiIdempotent uiIdempotent) {
    map.put(uiIdempotent.getId(), uiIdempotent);
  }
}

package org.open.solution.distributed.test.idempotent;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.annotation.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * IdempotentApp class
 *
 * @author nj
 * @date 2023/6/16
 **/
@RestController
@RequiredArgsConstructor
public class IdempotentApp {

  @Autowired
  private IdempotentService idempotentService;

  @PostMapping("/idempotent/block")
  @Idempotent(validateApi = "@idempotentService.validateData(#uiIdempotent)")
  public String idempotentBlock(@RequestBody UiIdempotent uiIdempotent) {

    idempotentService.add(uiIdempotent);
    return "1111";
  }
}

package org.open.solution.distributed.test.idempotent;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.annotation.DCLParamIdempotent;
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
  @DCLParamIdempotent(validateApi = "@idempotentService.validateData(#uiIdempotent)", enableProCheck = true, message = "操作次数过多")
  public String idempotentBlock(@RequestBody UiIdempotent uiIdempotent) {

    idempotentService.add(uiIdempotent);
    return "1111";
  }
}

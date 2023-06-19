package org.open.solution.distributed.test.idempotent;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.annotation.dcl.DCLParamIdempotent;
import org.open.solution.idempotent.annotation.dcl.DCLSpELIdempotent;
import org.open.solution.idempotent.annotation.state.StateParamIdempotent;
import org.open.solution.idempotent.annotation.state.StateSpELIdempotent;
import org.open.solution.idempotent.annotation.token.TokenIdempotent;
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

  @PostMapping("/idempotent/dcl")
  @DCLSpELIdempotent(
          validateApi = "@idempotentService.validateData(#uiIdempotent)",
          partKey = "#uiIdempotent.getId()",
          message = "dcl: 操作次数过多")
  public String idempotentDCL(@RequestBody UiIdempotent uiIdempotent) {

    idempotentService.add(uiIdempotent);
    return "1111";
  }

  @PostMapping("/idempotent/token")
  @TokenIdempotent(
          partKey = "#uiIdempotent.getId()",
          message = "token: 操作次数过多",
          resetException = true)
  public String idempotentToken(@RequestBody UiIdempotent uiIdempotent) {
    idempotentService.add(uiIdempotent);
    int i = 1/ 0;
    return "1111";
  }

  @PostMapping("/idempotent/state")
  @StateSpELIdempotent(
      partKey = "#uiIdempotent.getId()",
      message = "state: 操作次数过多",
      consumingExpirationDate = 5,
      consumedExpirationDate = 300)
  public String idempotentState(@RequestBody UiIdempotent uiIdempotent) {
    idempotentService.add(uiIdempotent);
    return "1111";
  }
}

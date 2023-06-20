package org.open.solution.distributed.test.idempotent;

import lombok.RequiredArgsConstructor;
import org.open.solution.idempotent.annotation.dlc.DLCSpELIdempotent;
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

  @PostMapping("/idempotent/dlc")
  @DLCSpELIdempotent(
          validateApi = "@idempotentService.validateData(#uiIdempotent)",
          partKey = "#uiIdempotent.getId()",
          message = "dlc: 操作次数过多")
  public String idempotentDLC(@RequestBody UiIdempotent uiIdempotent) {

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
      consumingExpirationDate = 6,
      consumedExpirationDate = 20,
      resetException = true)
  public String idempotentState(@RequestBody UiIdempotent uiIdempotent) throws InterruptedException {
    idempotentService.add(uiIdempotent);
//    Thread.sleep(5 * 1000);
    int i = 1/ uiIdempotent.getZ();
    return "1111";
  }
}

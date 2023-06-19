package org.open.solution.idempotent.core;

import org.open.solution.idempotent.enums.IdempotentSceneEnum;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.function.Function.identity;

/**
 * 幂等性校验级别工厂
 *
 * @author nj
 * @date 2023/6/15
 **/
public final class IdempotentSceneHandlerFactory {

  private final Map<IdempotentSceneEnum, IdempotentSceneHandler> idempotentSceneHandlers;

  public IdempotentSceneHandlerFactory(
      Set<IdempotentSceneHandler> idempotentSceneHandlers) {
    this.idempotentSceneHandlers = idempotentSceneHandlers.stream()
        .collect(Collectors.toMap(IdempotentSceneHandler::scene, identity()));
  }

  public IdempotentSceneHandler getInstance(IdempotentSceneEnum scene) {
    return idempotentSceneHandlers.get(scene);
  }
}

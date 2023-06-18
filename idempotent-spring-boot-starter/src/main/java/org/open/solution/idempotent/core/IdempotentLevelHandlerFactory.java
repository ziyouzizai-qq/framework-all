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
public final class IdempotentLevelHandlerFactory {

  private final Map<IdempotentSceneEnum, IdempotentLevelHandler> idempotentLevelHandlers;

  public IdempotentLevelHandlerFactory(
      Set<IdempotentLevelHandler> idempotentLevelHandlers) {
    this.idempotentLevelHandlers = idempotentLevelHandlers.stream()
        .collect(Collectors.toMap(IdempotentLevelHandler::scene, identity()));
  }

  public IdempotentLevelHandler getInstance(IdempotentSceneEnum scene) {
    return idempotentLevelHandlers.get(scene);
  }
}

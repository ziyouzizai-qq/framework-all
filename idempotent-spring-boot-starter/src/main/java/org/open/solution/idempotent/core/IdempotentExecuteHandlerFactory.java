package org.open.solution.idempotent.core;

import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.function.Function.identity;

/**
 * 幂等性处理器工厂
 */
public final class IdempotentExecuteHandlerFactory {

  private final Map<IdempotentTypeEnum, IdempotentExecuteHandler> idempotentExecuteHandlers;

  public IdempotentExecuteHandlerFactory(
      Set<IdempotentExecuteHandler> idempotentExecuteHandlers) {
    this.idempotentExecuteHandlers = idempotentExecuteHandlers.stream()
        .collect(Collectors.toMap(IdempotentExecuteHandler::type, identity()));
  }

  public IdempotentExecuteHandler getInstance(IdempotentTypeEnum type) {
    return idempotentExecuteHandlers.get(type);
  }
}

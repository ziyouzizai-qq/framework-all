package org.open.solution.idempotent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * state验证场景枚举
 */
@RequiredArgsConstructor
public enum IdempotentStateEnum {

  /**
   * 消费中
   */
  CONSUMING("0"),


  /**
   * 已消费
   */
  CONSUMED("1"),

  /**
   * 消费异常
   */
  CONSUME_ERROR("2");


  @Getter
  private final String code;

}

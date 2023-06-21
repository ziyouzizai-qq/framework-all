package org.open.solution.idempotent.enums;

/**
 * 幂等验证场景枚举
 */
public enum IdempotentSceneEnum {

  /**
   * 基于 DLC 场景验证
   */
  DLC,

  /**
   * 基于 TOKEN 场景验证
   */
  TOKEN,

  /**
   * 基于状态的验证
   */
  STATE
}

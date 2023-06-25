package org.open.solution.idempotent.annotation.dlc;

import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DLCIdempotent(type = IdempotentTypeEnum.PARAM)
public @interface DLCParamIdempotent {

  /**
   * DLC校验机制是否开启前置检查
   */
  @AliasFor(annotation = DLCIdempotent.class, attribute = "enableProCheck")
  boolean enableProCheck() default false;

  /**
   * BLOCK 情况需要业务层校验
   */
  @AliasFor(annotation = DLCIdempotent.class, attribute = "validateApi")
  String validateApi() default "";

  /**
   * 业务异常后是否重置当前token
   */
  @AliasFor(annotation = DLCIdempotent.class, attribute = "resetException")
  boolean resetException() default true;

  /**
   * 默认幂等失效期
   */
  @AliasFor(annotation = DLCIdempotent.class, attribute = "consumedExpirationDate")
  long consumedExpirationDate() default 60 * 10;

  /**
   * 触发幂等失败逻辑时，返回的错误提示信息
   */
  @AliasFor(annotation = DLCIdempotent.class, attribute = "message")
  String message() default "您操作太快，请稍后再试";

}

package org.open.solution.idempotent.annotation.state;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.STATE)
public @interface StateIdempotent {

  /**
   * {@link Idempotent#type} 的别名
   */
  @AliasFor(annotation = Idempotent.class, attribute = "type")
  IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

  /**
   * 触发幂等失败逻辑时，返回的错误提示信息
   */
  @AliasFor(annotation = Idempotent.class, attribute = "message")
  String message() default "您操作太快，请稍后再试";

  /**
   * 业务异常后是否重置当前token
   */
  @AliasFor(annotation = Idempotent.class, attribute = "resetException")
  boolean resetException() default true;

  /**
   * state模式下消费中的时效
   */
  @AliasFor(annotation = Idempotent.class, attribute = "consumingExpirationDate")
  long consumingExpirationDate() default 30;

  /**
   * state模式下消费完的时效
   */
  @AliasFor(annotation = Idempotent.class, attribute = "consumedExpirationDate")
  long consumedExpirationDate() default 60 * 10;
}

package org.open.solution.idempotent.annotation.state;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@StateIdempotent(type = IdempotentTypeEnum.SPEL)
public @interface StateSpELIdempotent {

  /**
   * 幂等Key，只有在 {@link Idempotent#type()} 为 {@link IdempotentTypeEnum#SPEL} 时生效
   */
  @AliasFor(annotation = Idempotent.class, attribute = "partKey")
  String partKey();

  /**
   * state模式下消费中的时效
   */
  @AliasFor(annotation = StateIdempotent.class, attribute = "consumingExpirationDate")
  long consumingExpirationDate() default 30;

  /**
   * state模式下消费完的时效
   */
  @AliasFor(annotation = StateIdempotent.class, attribute = "consumedExpirationDate")
  long consumedExpirationDate() default 60 * 10;

  /**
   * 业务异常后是否重置当前token
   */
  @AliasFor(annotation = StateIdempotent.class, attribute = "resetException")
  boolean resetException() default false;

  /**
   * 触发幂等失败逻辑时，返回的错误提示信息
   */
  @AliasFor(annotation = StateIdempotent.class, attribute = "message")
  String message() default "您操作太快，请稍后再试";
}

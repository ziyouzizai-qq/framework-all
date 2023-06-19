package org.open.solution.idempotent.annotation.state;

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
@StateIdempotent(type = IdempotentTypeEnum.PARAM)
public @interface StateParamIdempotent {

    /**
     * 幂等时效
     */
    @AliasFor(annotation = StateIdempotent.class, attribute = "expirationDate")
    long expirationDate() default 600;

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = StateIdempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";
}

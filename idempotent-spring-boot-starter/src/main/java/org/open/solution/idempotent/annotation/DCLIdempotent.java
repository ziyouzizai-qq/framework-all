package org.open.solution.idempotent.annotation;

import org.open.solution.idempotent.core.IdempotentDclHandler;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(level = IdempotentDclHandler.DCL)
public @interface DCLIdempotent {

    /**
     * {@link Idempotent#type} 的别名
     */
    @AliasFor(annotation = Idempotent.class, attribute = "type")
    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

    /**
     * DCL校验机制是否开启前置检查
     */
    @AliasFor(annotation = Idempotent.class, attribute = "enableProCheck")
    boolean enableProCheck() default false;

    /**
     * BLOCK 情况需要业务层校验
     */
    @AliasFor(annotation = Idempotent.class, attribute = "validateApi")
    String validateApi() default "@lockBlockHandler.validateData()";

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = Idempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";
}

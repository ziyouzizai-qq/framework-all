package org.open.solution.idempotent.annotation.dcl;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DCLIdempotent(type = IdempotentTypeEnum.SPEL)
public @interface DCLSpELIdempotent {

    /**
     * 幂等Key，只有在 {@link Idempotent#type()} 为 {@link IdempotentTypeEnum#SPEL} 时生效
     */
    @AliasFor(annotation = Idempotent.class, attribute = "partKey")
    String partKey();

    /**
     * DCL校验机制是否开启前置检查
     */
    @AliasFor(annotation = DCLIdempotent.class, attribute = "enableProCheck")
    boolean enableProCheck() default false;

    /**
     * BLOCK 情况需要业务层校验
     */
    @AliasFor(annotation = DCLIdempotent.class, attribute = "validateApi")
    String validateApi() default "@idempotentDclHandler.validateData()";

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = DCLIdempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";
}

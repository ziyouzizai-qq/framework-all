package org.open.solution.idempotent.annotation.dcl;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.DCL)
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
    String validateApi() default "@idempotentDclHandler.validateData()";

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = Idempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";
}

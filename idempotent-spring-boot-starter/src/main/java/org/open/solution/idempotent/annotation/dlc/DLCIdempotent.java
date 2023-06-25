package org.open.solution.idempotent.annotation.dlc;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.DLC)
public @interface DLCIdempotent {

    /**
     * {@link Idempotent#type} 的别名
     */
    @AliasFor(annotation = Idempotent.class, attribute = "type")
    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

    /**
     * DLC校验机制是否开启前置检查
     */
    @AliasFor(annotation = Idempotent.class, attribute = "enableProCheck")
    boolean enableProCheck() default false;

    /**
     * BLOCK 情况需要业务层校验
     */
    @AliasFor(annotation = Idempotent.class, attribute = "validateApi")
    String validateApi() default "";

    /**
     * 业务异常后是否重置当前token
     */
    @AliasFor(annotation = Idempotent.class, attribute = "resetException")
    boolean resetException() default true;

    /**
     * 默认幂等失效期
     */
    @AliasFor(annotation = Idempotent.class, attribute = "consumedExpirationDate")
    long consumedExpirationDate() default 60 * 10;

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = Idempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";

}

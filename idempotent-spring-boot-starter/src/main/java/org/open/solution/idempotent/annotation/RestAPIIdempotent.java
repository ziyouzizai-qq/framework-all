package org.open.solution.idempotent.annotation;

import org.open.solution.idempotent.enums.IdempotentSceneEnum;

import java.lang.annotation.*;

@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.RESTAPI)
public @interface RestAPIIdempotent {

//    /**
//     * {@link Idempotent#key} 的别名
//     */
//    @AliasFor(annotation = Idempotent.class, attribute = "key")
//    String key() default "";
//
//    /**
//     * {@link Idempotent#message} 的别名
//     */
//    @AliasFor(annotation = Idempotent.class, attribute = "message")
//    String message() default "您操作太快，请稍后再试";
//
//    /**
//     * {@link Idempotent#type} 的别名
//     */
//    @AliasFor(annotation = Idempotent.class, attribute = "type")
//    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;
}

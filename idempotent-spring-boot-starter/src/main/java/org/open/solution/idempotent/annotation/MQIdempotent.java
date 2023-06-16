package org.open.solution.idempotent.annotation;

import org.open.solution.idempotent.core.LockBlockHandler;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;

import java.lang.annotation.*;

@Deprecated
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent()
public @interface MQIdempotent {

//    /**
//     * {@link Idempotent#key} 的别名
//     */
//    @AliasFor(annotation = Idempotent.class, attribute = "key")
//    String key() default "";
//
//    /**
//     * {@link Idempotent#type} 的别名
//     */
//    @AliasFor(annotation = Idempotent.class, attribute = "type")
//    IdempotentTypeEnum type() default IdempotentTypeEnum.SPEL;
}

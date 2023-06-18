package org.open.solution.idempotent.annotation;

import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等Key，spEl 表达式
     */
    String partKey() default "";

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    String message() default "您操作太快，请稍后再试";

    /**
     * 验证幂等类型，支持多种幂等方式
     * RestAPI 建议使用 {@link IdempotentTypeEnum#TOKEN} 或 {@link IdempotentTypeEnum#PARAM}
     * 其它类型幂等验证，使用 {@link IdempotentTypeEnum#SPEL}
     */
    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

    /**
     * 验证幂等场景, 默认为DCL机制校验
     */
    IdempotentSceneEnum scene() default IdempotentSceneEnum.DCL;

    /**
     * DCL校验机制是否开启前置检查
     */
    boolean enableProCheck() default false;

    /**
     * BLOCK 情况需要业务层校验
     * @return
     */
    String validateApi() default "@idempotentDclHandler.validateData()";

}

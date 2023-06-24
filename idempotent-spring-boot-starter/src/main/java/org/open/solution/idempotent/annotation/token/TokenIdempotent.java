package org.open.solution.idempotent.annotation.token;

import org.open.solution.idempotent.annotation.Idempotent;
import org.open.solution.idempotent.enums.IdempotentSceneEnum;
import org.open.solution.idempotent.enums.IdempotentTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Idempotent(scene = IdempotentSceneEnum.TOKEN, type = IdempotentTypeEnum.TOKEN)
public @interface TokenIdempotent {

    /**
     * 通过spEl表达式获取token
     */
    @AliasFor(annotation = Idempotent.class, attribute = "partKey")
    String partKey() default "@idempotentTokenExecuteHandler.defaultToken()";

    /**
     * 业务异常后是否重置当前token
     */
    @AliasFor(annotation = Idempotent.class, attribute = "resetException")
    boolean resetException() default true;

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息
     */
    @AliasFor(annotation = Idempotent.class, attribute = "message")
    String message() default "您操作太快，请稍后再试";
}

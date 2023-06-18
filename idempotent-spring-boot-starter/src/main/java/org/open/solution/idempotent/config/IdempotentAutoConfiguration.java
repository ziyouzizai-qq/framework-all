package org.open.solution.idempotent.config;

import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.*;
import org.open.solution.idempotent.core.param.IdempotentParamExecuteHandler;
import org.open.solution.idempotent.core.spel.IdempotentSpELExecuteHandler;
import org.open.solution.idempotent.core.token.IdempotentTokenController;
import org.open.solution.idempotent.core.token.IdempotentTokenExecuteHandler;
import org.open.solution.idempotent.core.token.IdempotentTokenHandler;
import org.open.solution.idempotent.core.token.IdempotentTokenService;
import org.open.solution.idempotent.toolkit.SpELParser;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

/**
 * 幂等自动装配包
 *
 * @author nj
 * @date 2023/6/15
 **/
@Configuration
public class IdempotentAutoConfiguration {

  /**
   * 幂等切面
   */
  @Bean
  public IdempotentAspect idempotentAspect(IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory,
                                           IdempotentLevelHandlerFactory idempotentLevelHandlerFactory) {
    return new IdempotentAspect(idempotentExecuteHandlerFactory, idempotentLevelHandlerFactory);
  }

  /**
   * 幂等执行器工厂
   */
  @Bean
  public IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory(
      Set<IdempotentExecuteHandler> idempotentExecuteHandlers) {
    return new IdempotentExecuteHandlerFactory(idempotentExecuteHandlers);
  }

  /**
   * 全参数幂等器
   */
  @Bean
  public IdempotentParamExecuteHandler idempotentParamExecuteHandler() {
    return new IdempotentParamExecuteHandler();
  }

  /**
   * 通过spel解析，部分参数幂等器
   */
  @Bean
  public IdempotentSpELExecuteHandler idempotentSpELExecuteHandler(SpELParser spELParser) {
    return new IdempotentSpELExecuteHandler(spELParser);
  }

  /**
   * token幂等器
   */
  @Bean
  public IdempotentTokenExecuteHandler idempotentTokenExecuteHandler(SpELParser spELParser,
                                                                     StringRedisTemplate stringRedisTemplate) {
    return new IdempotentTokenExecuteHandler(spELParser, stringRedisTemplate);
  }

  /**
   * 幂等模式级别工厂
   */
  @Bean
  public IdempotentLevelHandlerFactory idempotentLevelHandlerFactory(
      Set<IdempotentLevelHandler> idempotentLevelHandlers) {
    return new IdempotentLevelHandlerFactory(idempotentLevelHandlers);
  }

  /**
   * DCL模式幂等
   * @param distributedLockFactory 分布式锁工厂
   * @param spELParser spel解析器
   */
  @Bean
  public IdempotentDclHandler idempotentDclHandler(DistributedLockFactory distributedLockFactory, SpELParser spELParser) {
    return new IdempotentDclHandler(distributedLockFactory, spELParser);
  }

  /**
   * token模式幂等
   */
  @Bean
  public IdempotentTokenHandler idempotentTokenHandler(StringRedisTemplate stringRedisTemplate) {
    return new IdempotentTokenHandler(stringRedisTemplate);
  }

  /**
   * spel解析器
   */
  @Bean
  public SpELParser spELParser(BeanFactory beanFactory) {
    return new SpELParser(beanFactory);
  }

  /**
   * token模式下，获取token接口
   */
  @Bean
  public IdempotentTokenController idempotentTokenController(IdempotentTokenService idempotentTokenService) {
    return new IdempotentTokenController(idempotentTokenService);
  }

}

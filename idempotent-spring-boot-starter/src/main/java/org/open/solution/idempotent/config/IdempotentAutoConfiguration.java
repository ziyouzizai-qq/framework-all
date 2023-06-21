package org.open.solution.idempotent.config;

import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.*;
import org.open.solution.idempotent.core.scene.dlc.IdempotentDLCHandler;
import org.open.solution.idempotent.core.scene.state.IdempotentStateHandler;
import org.open.solution.idempotent.core.type.param.IdempotentParamExecuteHandler;
import org.open.solution.idempotent.core.type.spel.IdempotentSpELExecuteHandler;
import org.open.solution.idempotent.core.type.token.IdempotentTokenController;
import org.open.solution.idempotent.core.type.token.IdempotentTokenExecuteHandler;
import org.open.solution.idempotent.core.scene.token.IdempotentTokenHandler;
import org.open.solution.idempotent.core.type.token.IdempotentTokenService;
import org.open.solution.idempotent.toolkit.SpELParser;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties({IdempotentTokenProperties.class})
public class IdempotentAutoConfiguration {

  /**
   * 幂等切面
   */
  @Bean
  public IdempotentAspect idempotentAspect(IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory,
                                           IdempotentSceneHandlerFactory idempotentSceneHandlerFactory) {
    return new IdempotentAspect(idempotentExecuteHandlerFactory, idempotentSceneHandlerFactory);
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
                                                                     StringRedisTemplate stringRedisTemplate,
                                                                     IdempotentTokenProperties idempotentTokenProperties) {
    return new IdempotentTokenExecuteHandler(spELParser, stringRedisTemplate, idempotentTokenProperties);
  }

  /**
   * 幂等模式级别工厂
   */
  @Bean
  public IdempotentSceneHandlerFactory idempotentSceneHandlerFactory(
      Set<IdempotentSceneHandler> idempotentSceneHandlers) {
    return new IdempotentSceneHandlerFactory(idempotentSceneHandlers);
  }

  /**
   * DLC模式幂等
   *
   * @param distributedLockFactory 分布式锁工厂
   * @param spELParser             spel解析器
   */
  @Bean
  public IdempotentDLCHandler idempotentDLCHandler(DistributedLockFactory distributedLockFactory,
                                                   SpELParser spELParser) {
    return new IdempotentDLCHandler(distributedLockFactory, spELParser);
  }

  /**
   * token模式幂等
   */
  @Bean
  public IdempotentTokenHandler idempotentTokenHandler(StringRedisTemplate stringRedisTemplate,
                                                       IdempotentTokenProperties idempotentTokenProperties) {
    return new IdempotentTokenHandler(stringRedisTemplate, idempotentTokenProperties);
  }

  /**
   * state模式幂等
   */
  @Bean
  public IdempotentStateHandler idempotentStateHandler(StringRedisTemplate stringRedisTemplate) {
    return new IdempotentStateHandler(stringRedisTemplate);
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

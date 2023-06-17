package org.open.solution.idempotent.config;

import org.open.solution.distributed.lock.core.DistributedLockFactory;
import org.open.solution.idempotent.core.IdempotentAspect;
import org.open.solution.idempotent.core.IdempotentExecuteHandler;
import org.open.solution.idempotent.core.IdempotentExecuteHandlerFactory;
import org.open.solution.idempotent.core.IdempotentLevelHandler;
import org.open.solution.idempotent.core.IdempotentLevelHandlerFactory;
import org.open.solution.idempotent.core.IdempotentDclHandler;
import org.open.solution.idempotent.core.param.IdempotentParamExecuteHandler;
import org.open.solution.idempotent.toolkit.SpELParser;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

/**
 * 幂等自动装配包
 *
 * @author nj
 * @date 2023/6/15
 **/
//@EnableConfigurationProperties(IdempotentProperties.class)
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

  @Bean
  public IdempotentExecuteHandlerFactory idempotentExecuteHandlerFactory(
      Set<IdempotentExecuteHandler> idempotentExecuteHandlers) {
    return new IdempotentExecuteHandlerFactory(idempotentExecuteHandlers);
  }

  @Bean
  public IdempotentParamExecuteHandler idempotentParamExecuteHandler() {
    return new IdempotentParamExecuteHandler();
  }

  @Bean
  public IdempotentLevelHandlerFactory idempotentLevelHandlerFactory(
      Set<IdempotentLevelHandler> idempotentLevelHandlers) {
    return new IdempotentLevelHandlerFactory(idempotentLevelHandlers);
  }

  /**
   * 区域锁
   * @param distributedLockFactory 分布式锁工厂
   * @param spELParser spel解析器
   */
  @Bean
  public IdempotentDclHandler lockBlockHandler(DistributedLockFactory distributedLockFactory, SpELParser spELParser) {
    return new IdempotentDclHandler(distributedLockFactory, spELParser);
  }

  @Bean
  public SpELParser spELParser(BeanFactory beanFactory) {
    return new SpELParser(beanFactory);
  }


//
//  /**
//   * 参数方式幂等实现，基于 RestAPI 场景
//   */
//  @Bean
//  @ConditionalOnMissingBean
//  public IdempotentParamService idempotentParamExecuteHandler(RedissonClient redissonClient) {
//    return new IdempotentParamExecuteHandler(redissonClient);
//  }
//
//  /**
//   * Token 方式幂等实现，基于 RestAPI 场景
//   */
//  @Bean
//  @ConditionalOnMissingBean
//  public IdempotentTokenService idempotentTokenExecuteHandler(DistributedCache distributedCache,
//                                                              IdempotentProperties idempotentProperties) {
//    return new IdempotentTokenExecuteHandler(distributedCache, idempotentProperties);
//  }
//
//  /**
//   * 申请幂等 Token 控制器，基于 RestAPI 场景
//   */
//  @Bean
//  public IdempotentTokenController idempotentTokenController(IdempotentTokenService idempotentTokenService) {
//    return new IdempotentTokenController(idempotentTokenService);
//  }
//
//  /**
//   * SpEL 方式幂等实现，基于 RestAPI 场景
//   */
//  @Bean
//  @ConditionalOnMissingBean
//  public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient) {
//    return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
//  }
//
//  /**
//   * SpEL 方式幂等实现，基于 MQ 场景
//   */
//  @Bean
//  @ConditionalOnMissingBean
//  public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(DistributedCache distributedCache) {
//    return new IdempotentSpELByMQExecuteHandler(distributedCache);
//  }
}

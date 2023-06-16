package org.open.solution.distributed.lock.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * AutoConfigureAfterExclude class
 *
 * @author nj
 * @date 2023/6/16
 **/
public class AutoConfigureAfterExclude implements BeanFactoryPostProcessor, Ordered {

  private final Class<?>[] autoConfigurations;

  public AutoConfigureAfterExclude(Class<?>... autoConfigurations) {
    this.autoConfigurations = autoConfigurations;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    for (Class<?> configuration : autoConfigurations) {
      BeanDefinition definition;
      if (beanFactory.containsBeanDefinition(configuration.getName())) {
        definition = beanFactory.getBeanDefinition(configuration.getName());
        definition.setAutowireCandidate(false);
      }
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }

}

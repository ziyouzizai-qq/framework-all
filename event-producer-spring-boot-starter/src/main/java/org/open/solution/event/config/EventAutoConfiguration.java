package org.open.solution.event.config;

import org.open.solution.event.core.ChangeLogEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EventAutoConfiguration class
 *
 * @author nj
 * @date 2024/2/29
 **/
@Configuration
public class EventAutoConfiguration {

  @Bean
  public ChangeLogEventListener changeLogEventListener(EventProperties eventProperties) {
    return new ChangeLogEventListener(eventProperties);
  }
}

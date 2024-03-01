package org.open.solution.event.core;

import org.open.solution.event.config.EventProperties;
import org.springframework.context.ApplicationListener;

/**
 * ChangeLogEventListener class
 *
 * @author nj
 **/
public class ChangeLogEventListener implements ApplicationListener<ChangeLogEvent> {

  private final EventProperties eventProperties;

  public ChangeLogEventListener(EventProperties eventProperties) {
    this.eventProperties = eventProperties;
  }

  @Override
  public void onApplicationEvent(ChangeLogEvent event) {
    int payLoadSize = event.getPayLoads().size();
    
  }
}

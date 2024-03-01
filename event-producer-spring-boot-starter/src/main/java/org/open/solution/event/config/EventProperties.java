package org.open.solution.event.config;

//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * EventProperties class
 *
 * @author nj
 * @date 2023/6/13
 **/
//@ConfigurationProperties("open.solution.idempotent.token")
//@Getter
//@Setter
public class EventProperties {

  /**
   * 批次数据量
   */
  private Integer batchSize = 100;

}

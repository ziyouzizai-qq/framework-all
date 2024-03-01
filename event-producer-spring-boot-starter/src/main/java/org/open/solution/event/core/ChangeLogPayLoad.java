package org.open.solution.event.core;

import org.open.solution.event.core.enums.ChangeType;

/**
 * ChangeLogPayLoad class
 *
 * @author nj
 **/
public class ChangeLogPayLoad {

  private final String subject;

  private final ChangeType changeType;

  private final Object beforeValue;

  private final Object afterValue;

  private String value;

  /**
   * batchid + seqNum解决消费端幂等性
   */
  private Integer seqNum;

  public ChangeLogPayLoad(Object beforeValue, String subject, ChangeType changeType, Object afterValue, String value) {
    this.subject = subject;
    this.changeType = changeType;
    this.beforeValue = beforeValue;
    this.afterValue = afterValue;
    this.value = value;
  }

  public Object getBeforeValue() {
    return beforeValue;
  }

  public Object getAfterValue() {
    return afterValue;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Integer getSeqNum() {
    return seqNum;
  }

  public void setSeqNum(Integer seqNum) {
    this.seqNum = seqNum;
  }
}

package org.open.solution.event.core;

import org.springframework.context.ApplicationEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChangeLogEvent extends ApplicationEvent {

  private final List<ChangeLogPayLoad> payLoads = new LinkedList<>();

  private final String batchId;

  private int num = 0;

  public ChangeLogEvent(Object source) {
    super(source);
    batchId = UUID.randomUUID().toString();
  }

  public String getBatchId() {
    return batchId;
  }

  public void addChangeLogPayLoad(ChangeLogPayLoad payLoad) {
    payLoad.setSeqNum(num);
    payLoads.add(payLoad);
    num++;
  }

  public List<ChangeLogPayLoad> getPayLoads() {
    return payLoads;
  }
}

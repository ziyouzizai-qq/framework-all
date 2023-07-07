package org.open.solution.idempotent.core;

/**
 * AbstractIdempotentSceneHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public abstract class AbstractIdempotentSceneHandler implements IdempotentSceneHandler {

  @Override
  public void postProcessing() {
    handleProcessing(IdempotentContext.removeLast());
  }

  @Override
  public void exceptionProcessing() {

  }

  public void handleProcessing(Object param) {
  }

}

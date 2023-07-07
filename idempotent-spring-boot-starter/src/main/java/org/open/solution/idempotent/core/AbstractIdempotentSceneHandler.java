package org.open.solution.idempotent.core;

/**
 * AbstractIdempotentSceneHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public abstract class AbstractIdempotentSceneHandler<D> implements IdempotentSceneHandler {

  public abstract D putContext(IdempotentValidateParam param);

  public abstract void doValidate(D wrapper);

  @Override
  public void validateIdempotent(IdempotentValidateParam param) {
    D wrapper = putContext(param);
    IdempotentContext.put(wrapper);
    doValidate(wrapper);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void postProcessing() {
    handleProcessing((D) IdempotentContext.removeLast());
  }

  @Override
  @SuppressWarnings("unchecked")
  public void exceptionProcessing() {
    handleExProcessing((D) IdempotentContext.get());
  }

  public void handleProcessing(D wrapper) {
  }

  public void handleExProcessing(D wrapper) {
  }

}

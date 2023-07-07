package org.open.solution.idempotent.core;

/**
 * AbstractIdempotentSceneHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public abstract class AbstractIdempotentSceneHandler<D> implements IdempotentSceneHandler {

  public abstract D putContext(IdempotentValidateParam param);

  public abstract void doValidate(D data);

  @Override
  public void validateIdempotent(IdempotentValidateParam param) {
    D data = putContext(param);
    IdempotentContext.put(data);
    doValidate(data);
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

  public void handleProcessing(D param) {
  }

  public void handleExProcessing(D param) {
  }

}

package org.open.solution.idempotent.core;

/**
 * AbstractIdempotentSceneHandler class
 *
 * @author nj
 * @date 2023/6/15
 **/
public abstract class AbstractIdempotentSceneHandler<WRAPPER> implements IdempotentSceneHandler {

  public abstract WRAPPER generateWrapper(IdempotentValidateParam param);

  public abstract void doValidate(WRAPPER wrapper);

  @Override
  public void validateIdempotent(IdempotentValidateParam param) {
    WRAPPER wrapper = generateWrapper(param);
    IdempotentContext.put(wrapper);
    doValidate(wrapper);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void postProcessing() {
    handleProcessing((WRAPPER) IdempotentContext.removeLast());
  }

  @Override
  @SuppressWarnings("unchecked")
  public void exceptionProcessing() {
    handleExProcessing((WRAPPER) IdempotentContext.get());
  }

  public void handleProcessing(WRAPPER wrapper) {
  }

  public void handleExProcessing(WRAPPER wrapper) {
  }

}

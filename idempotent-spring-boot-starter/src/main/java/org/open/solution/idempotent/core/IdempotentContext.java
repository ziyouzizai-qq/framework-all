package org.open.solution.idempotent.core;

import org.springframework.util.CollectionUtils;
import java.util.Deque;
import java.util.LinkedList;

/**
 * IdempotentContext class
 *
 * @author nj
 * @date 2023/6/15
 **/
public class IdempotentContext {

  private static final ThreadLocal<Deque<Object>> CONTEXT = new ThreadLocal<>();

  /**
   * 将对象缓存到当前线程中
   *
   * @param obj
   */
  public static void put(Object obj) {
    Deque<Object> objList = CONTEXT.get();
    if (CollectionUtils.isEmpty(objList)) {
      objList = new LinkedList<>();
      CONTEXT.set(objList);
    }
    objList.add(obj);
  }

  /**
   * 获取对象且从当前线程中移除
   */
  public static Object removeLast() {
    Deque<Object> objList = CONTEXT.get();
    Object obj = objList.removeLast();
    if (CollectionUtils.isEmpty(objList)) {
      CONTEXT.remove();
    }
    return obj;
  }

  /**
   * 获取对象
   */
  public static Object get() {
    Deque<Object> objList = CONTEXT.get();
    return objList.getLast();
  }
}

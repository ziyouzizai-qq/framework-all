package org.open.solution.idempotent.toolkit;

import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.lang.reflect.Method;

/**
 * SpELParser class
 *
 * @author nj
 * @date 2023/6/15
 **/
@RequiredArgsConstructor
public class SpELParser {

  private final BeanFactory beanFactory;

  /**
   * 校验并返回实际使用的 spEL 表达式
   *
   * @param spEl spEL 表达式
   * @return 实际使用的 spEL 表达式
   */
  public Object parseKey(String spEl, Method method, Object[] contextObj) {
    String spElFlag = "#";
    if (!spEl.contains(spElFlag)) {
      return spEl;
    }
    return parse(spEl, method, contextObj);
  }

  /**
   * 转换参数为字符串
   *
   * @param spEl       spEl 表达式
   * @param contextObj 上下文对象
   * @return 解析的字符串值
   */
  public Object parse(String spEl, Method method, Object[] contextObj) {
    LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    ExpressionParser parser = new SpelExpressionParser();
    String[] params = discoverer.getParameterNames(method);
    StandardEvaluationContext context = new StandardEvaluationContext();
    if (ArrayUtil.isNotEmpty(params)) {
      for (int len = 0; len < params.length; len++) {
        context.setVariable(params[len], contextObj[len]);
      }
    }
    context.setBeanResolver(new BeanFactoryResolver(beanFactory));
    return parser.parseExpression(spEl).getValue(context);
  }

}

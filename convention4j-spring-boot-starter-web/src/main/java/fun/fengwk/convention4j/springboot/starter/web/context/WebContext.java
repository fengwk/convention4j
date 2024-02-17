package fun.fengwk.convention4j.springboot.starter.web.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author fengwk
 */
public interface WebContext {

    /**
     * 获取当前作用域的http请求
     *
     * @return http请求
     */
    HttpServletRequest getRequest();

    /**
     * 获取当前作用域的http响应
     *
     * @return http响应
     */
    HttpServletResponse getResponse();

    /**
     * 设置属性
     *
     * @param key   key
     * @param value value
     */
    void setAttribute(String key, Object value);

    /**
     * 获取属性
     *
     * @param key key
     * @return value
     */
    Object getAttribute(String key);

    /**
     * 向当前作用域设置http请求和响应
     *
     * @param request  http请求
     * @param response http响应
     */
    void set(HttpServletRequest request, HttpServletResponse response);

    /**
     * 清理当前作用域的http请求和响应
     */
    void clear();

}

package fun.fengwk.convention4j.oauth2.sdk.context.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author fengwk
 */
public class ThreadLocalOAuth2ContextInterceptor<SUBJECT> implements HandlerInterceptor {

    @Autowired
    private volatile ThreadLocalOAuth2Context<SUBJECT> threadLocalOAuth2Context;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        threadLocalOAuth2Context.set(request, response);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocalOAuth2Context.clear();
    }

}

package fun.fengwk.convention4j.springboot.starter.web.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.Objects;

/**
 * @author fengwk
 */
public class WebContextInterceptor implements AsyncHandlerInterceptor {

    private final WebContext webContext;

    public WebContextInterceptor(WebContext webContext) {
        this.webContext = Objects.requireNonNull(webContext, "webContext cannot be null");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        webContext.set(request, response);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        webContext.clear();
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是异步请求提前清理掉上下文，否则在其它线程中可能会错误地清理上下文
        webContext.clear();
    }

}

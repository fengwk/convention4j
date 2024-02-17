package fun.fengwk.convention4j.springboot.starter.web.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * @author fengwk
 */
public class WebContextInterceptor implements HandlerInterceptor {

    private final WebContext webContext;

    public WebContextInterceptor(WebContext webContext) {
        this.webContext = Objects.requireNonNull(webContext, "webContext cannot be null");
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        webContext.set(request, response);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        webContext.clear();
    }

}

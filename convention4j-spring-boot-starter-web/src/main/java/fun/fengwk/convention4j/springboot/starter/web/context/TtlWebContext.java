package fun.fengwk.convention4j.springboot.starter.web.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class TtlWebContext implements WebContext {

    private final TransmittableThreadLocal<Context> tlCtx = new TransmittableThreadLocal<>();

    @Override
    public HttpServletRequest getRequest() {
        Context ctx = tlCtx.get();
        if (ctx != null) {
            return ctx.getRequest();
        }
        return null;
    }

    @Override
    public HttpServletResponse getResponse() {
        Context ctx = tlCtx.get();
        if (ctx != null) {
            return ctx.getResponse();
        }
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {
        Context ctx = tlCtx.get();
        if (ctx != null) {
            ctx.getAttributes().put(key, value);
        }
    }

    @Override
    public Object getAttribute(String key) {
        Context ctx = tlCtx.get();
        if (ctx != null) {
            return ctx.getAttributes().get(key);
        }
        return null;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response) {
        tlCtx.set(new Context(request, response));
    }

    @Override
    public void clear() {
        tlCtx.remove();
    }

    @Data
    static class Context {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final Map<String, Object> attributes = new HashMap<>();
    }

}

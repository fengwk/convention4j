package fun.fengwk.convention4j.springboot.starter.web.tracer;

import fun.fengwk.convention4j.tracer.propagation.Formats;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * @author fengwk
 */
public class TracerWebInterceptor implements AsyncHandlerInterceptor {

    private final static String CONTEXT_ATTRIBUTE = TracerWebInterceptor.class.getName() + ".context";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Tracer tracer = GlobalTracer.get();
        // 获取父级上下文
        SpanContext parentContext = tracer.extract(Formats.HTTP_SERVLET_REQUEST_EXTRACT, request);
        // 构建span
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(parseOperationName(request, handler))
            .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_SERVER)
            .withTag(Tags.HTTP_METHOD, request.getMethod())
            .withTag(Tags.HTTP_URL, request.getRequestURI());
        if (parentContext != null) {
            spanBuilder.asChildOf(parentContext);
        }
        // 开启span
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        // 设置到请求作用域中
        request.setAttribute(CONTEXT_ATTRIBUTE, new Context(span, scope));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        finishSpan(request, response, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        finishSpan(request, response, null);
    }

    private void finishSpan(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Context context = getAttributeAndClear(request, CONTEXT_ATTRIBUTE, Context.class);
        if (context == null) {
            return;
        }
        Span span = context.getSpan();
        try (Scope ignored = context.getScope()) {
            int status = response.getStatus();
            if (ex != null || status < 200 || status >= 300) {
                span.setTag(Tags.ERROR, true);
                if (ex != null) {
                    span.log(ex.getMessage());
                }
            } else {
                span.setTag(Tags.ERROR, false);
            }
            span.setTag(Tags.HTTP_STATUS, status);
        } finally {
            span.finish();
        }
    }

    private <T> T getAttributeAndClear(HttpServletRequest request, String attrName, Class<T> clazz) {
        Object obj = request.getAttribute(attrName);
        if (obj == null) {
            return null;
        }
        request.removeAttribute(attrName);
        if (!clazz.isAssignableFrom(obj.getClass())) {
            return null;
        }
        return clazz.cast(obj);
    }

    private String parseOperationName(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Object bean = hm.getBean();
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            RequestMapping classReqMappingAnno = AnnotatedElementUtils.findMergedAnnotation(
                targetClass, RequestMapping.class);
            RequestMapping methodReqMappingAnno = AnnotatedElementUtils.findMergedAnnotation(
                targetClass, RequestMapping.class);
            String[] typePaths = new String[0];
            String[] methodPaths = new String[0];
            if (classReqMappingAnno != null) {
                typePaths = classReqMappingAnno.value();
            }
            if (methodReqMappingAnno != null) {
                methodPaths = methodReqMappingAnno.value();
            }
            if (typePaths.length > 0 && methodPaths.length > 0) {
                for (String classPath : typePaths) {
                    for (String methodPath : methodPaths) {
                        return classPath + methodPath;
                    }
                }
            } else if (methodPaths.length > 0) {
                for (String methodPath : methodPaths) {
                    return methodPath;
                }
            }  else { // typePaths.length > 0
                for (String typePath : typePaths) {
                    return typePath;
                }
            }
        }
        return request.getRequestURI();
    }

    @Data
    static class Context {

        private final Span span;
        private final Scope scope;

    }

}

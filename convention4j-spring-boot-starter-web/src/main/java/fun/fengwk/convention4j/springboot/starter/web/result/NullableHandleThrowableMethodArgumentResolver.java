package fun.fengwk.convention4j.springboot.starter.web.result;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 支持{@link WebExceptionResultHandlerChain#handleThrowable(Exception, HttpServletRequest, WebRequest, HandlerMethod)}
 * 方法中{@link HandlerMethod}参数为null的情况。
 * @see InvocableHandlerMethod#getMethodArgumentValues(NativeWebRequest, ModelAndViewContainer, Object...)
 * @author fengwk
 */
public class NullableHandleThrowableMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Parameter HANDLE_THROWABLE_METHOD_PARAMETER;

    static {
        try {
            Method handleThrowableMethod = WebExceptionResultHandlerChain.class.getMethod(
                "handleThrowable", Exception.class, HttpServletRequest.class, WebRequest.class, HandlerMethod.class);
            HANDLE_THROWABLE_METHOD_PARAMETER = handleThrowableMethod.getParameters()[3];
        } catch (NoSuchMethodException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Objects.equals(HANDLE_THROWABLE_METHOD_PARAMETER, parameter.getParameter());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        // 允许返回null
        return null;
    }

}

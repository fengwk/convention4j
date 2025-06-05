package fun.fengwk.convention4j.ai.tool;

import fun.fengwk.convention4j.common.json.JsonUtils;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class ReflectChatToolHandler implements ToolFunctionHandler {

    /**
     * 函数名称
     */
    private String name;

    /**
     * 函数描述
     */
    private String description;

    /**
     * 函数的参数
     */
    private JsonSchema parameters;

    /**
     * 执行函数的目标方法
     */
    private Method method;

    /**
     * 执行函数的目标对象
     */
    private Object target;

    @Override
    public String call(String arguments) {
        // 解析传入参数
        LinkedHashMap<String, Object> argumentMap = JsonUtils.fromJson(arguments, LinkedHashMap.class);

        // 构建调用参数
        LinkedHashMap<String, JsonSchema> properties = parameters.getProperties();
        Object[] params = new Object[properties.size()];
        int idx = 0;
        for (Map.Entry<String, JsonSchema> entry : properties.entrySet()) {
            Object val = argumentMap.get(entry.getKey());
            String jsonVal = JsonUtils.toJson(val);
            params[idx] = JsonUtils.fromJson(jsonVal, entry.getValue().getJavaType());
            idx++;
        }

        // 执行目标方法
        Object ret = invokeMethod(method, target, params);
        return String.valueOf(ret);
    }

    private Object invokeMethod(Method method, Object target, Object[] params) {
        try {
            return method.invoke(target, params);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    private void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            rethrowRuntimeException(ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    private void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

}

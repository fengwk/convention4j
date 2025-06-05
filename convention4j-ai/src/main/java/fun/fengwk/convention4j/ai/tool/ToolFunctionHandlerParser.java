package fun.fengwk.convention4j.ai.tool;

import fun.fengwk.convention4j.ai.tool.annotation.ToolFunction;
import fun.fengwk.convention4j.ai.tool.annotation.ToolFunctionParam;
import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.reflect.TypeResolver;

import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 解析带有{@link ToolFunction}注解的函数
 *
 * @author fengwk
 */
public class ToolFunctionHandlerParser {

    /**
     * 解析 bean 中的所有工具并注册到注册表中
     *
     * @param bean bean
     * @param registry 注册表
     */
    public static void parseAndRegister(Object bean, ToolFunctionHandlerRegistry registry) {
        ToolFunctionHandlerParser toolFunctionParser = new ToolFunctionHandlerParser();
        List<ToolFunctionHandler> handlers = toolFunctionParser.parse(bean);
        handlers.forEach(registry::registerHandler);
    }

    public List<ToolFunctionHandler> parse(Object bean) {
        List<ToolFunctionHandler> handlers = new ArrayList<>();
        doParse(bean, handlers);
        return handlers;
    }

    private void doParse(Object bean, List<ToolFunctionHandler> handlers) {
        for (Method method : ClassUtils.getAllDeclaredMethods(bean.getClass())) {
            ToolFunction toolFunction = ClassUtils.findAnnotation(method, ToolFunction.class);
            if (toolFunction == null) {
                continue;
            }

            if (!CharSequence.class.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException("@ToolFunction should return CharSequence type");
            }

            ReflectChatToolHandler toolFunctionHandler = new ReflectChatToolHandler();
            toolFunctionHandler.setName(getToolName(toolFunction, method));
            toolFunctionHandler.setDescription(toolFunction.description());
            toolFunctionHandler.setParameters(parseParameters(method));
            toolFunctionHandler.setMethod(method);
            toolFunctionHandler.setTarget(bean);
            handlers.add(toolFunctionHandler);
        }
    }

    private JsonSchema parseParameters(Method method) {
        JsonSchema root = new JsonSchema();
        root.setType("object");
        root.setProperties(new LinkedHashMap<>());
        root.setRequired(new ArrayList<>());
        for (Parameter parameter : method.getParameters()) {
            ToolFunctionParam toolFunctionParam = ClassUtils.findAnnotation(parameter, ToolFunctionParam.class);
            String name = getParamName(toolFunctionParam, parameter);
            JsonSchema property = parseParameter(parameter.getParameterizedType(),
                String.format("%s->%s", method.getName(), name));
            root.getProperties().put(name, property);
            property.setDescription(getDescription(toolFunctionParam));
            if (getRequired(toolFunctionParam)) {
                root.getRequired().add(name);
            }
        }
        return root;
    }

    private JsonSchema parseParameter(Type type, String pathTrace) {
        JsonSchema jsonSchema = new JsonSchema();
        jsonSchema.setJavaType(type);

        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            clazz = ClassUtils.boxedIfPrimitiveType(clazz);
            if (Byte.class.isAssignableFrom(clazz)
                || Short.class.isAssignableFrom(clazz)
                || Integer.class.isAssignableFrom(clazz)
                || Long.class.isAssignableFrom(clazz)
                || BigInteger.class.isAssignableFrom(clazz)
                || AtomicInteger.class.isAssignableFrom(clazz)
                || AtomicLong.class.isAssignableFrom(clazz)
                || LongAdder.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("integer");
            } else if (Number.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("number");
            } else if (CharSequence.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("string");
            } else if (Boolean.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("boolean");
            } else if (Collection.class.isAssignableFrom(clazz)) {
                jsonSchema.setType("array");
                JsonSchema any = new JsonSchema();
                any.setType("any");
                jsonSchema.setItems(any);
            } else if (clazz.isArray()) {
                jsonSchema.setType("array");
                jsonSchema.setItems(parseParameter(clazz.getComponentType(), pathTrace + "[]"));
            } else {
                jsonSchema.setType("object");
                jsonSchema.setProperties(new LinkedHashMap<>());
                for (Field field : ClassUtils.getAllDeclaredFields(clazz)) {
                    jsonSchema.getProperties().put(field.getName(),
                        parseParameter(field.getGenericType(), pathTrace + "->" + field.getName()));
                }
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) rawType)) {
                ParameterizedType collectionPt = new TypeResolver(type).as(Collection.class).asParameterizedType();
                jsonSchema.setType("array");
                jsonSchema.setItems(parseParameter(collectionPt.getActualTypeArguments()[0], pathTrace + "[]"));
            } else {
                jsonSchema.setType("any");
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            jsonSchema.setType("array");
            jsonSchema.setItems(parseParameter(gat.getGenericComponentType(), pathTrace + "[]"));
        } else {
            jsonSchema.setType("any");
        }

        return jsonSchema;
    }

    private String getToolName(ToolFunction toolFunction, Method method) {
        String name = toolFunction.name();
        if (StringUtils.isNotEmpty(name)) {
            return name;
        }
        return method.getName();
    }

    private String getParamName(ToolFunctionParam toolFunctionParam, Parameter parameter) {
        if (toolFunctionParam != null) {
            String name = toolFunctionParam.name();
            if (StringUtils.isNotEmpty(name)) {
                return name;
            }
        }
        return parameter.getName();
    }

    private String getDescription(ToolFunctionParam toolFunctionParam) {
        if (toolFunctionParam != null) {
            return toolFunctionParam.description();
        }
        return StringUtils.EMPTY;
    }

    private boolean getRequired(ToolFunctionParam toolFunctionParam) {
        if (toolFunctionParam != null) {
            return toolFunctionParam.required();
        }
        return false;
    }

}

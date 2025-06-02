package fun.fengwk.convention4j.common.chat.tool;

import fun.fengwk.convention4j.common.chat.tool.annotation.ChatTool;
import fun.fengwk.convention4j.common.chat.tool.annotation.ChatToolParam;
import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.common.reflect.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 解析带有{@link ChatTool}注解的函数
 *
 * @author fengwk
 */
public class ChatToolHandlerParser {

    public List<ChatToolHandler> parse(Object object) {
        List<ChatToolHandler> handlers = new ArrayList<>();
        doParse(object, handlers);
        return handlers;
    }

    private void doParse(Object object, List<ChatToolHandler> handlers) {
        for (Method method : getAllDeclaredMethods(object.getClass())) {
            ChatTool toolFunction = findAnnotation(method, ChatTool.class);
            if (toolFunction == null) {
                continue;
            }

            if (!CharSequence.class.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException("@ToolFunction should return CharSequence type");
            }

            ReflectChatToolHandler toolFunctionHandler = new ReflectChatToolHandler();
            toolFunctionHandler.setName(method.getDeclaringClass().getSimpleName() + "_" + method.getName());
            toolFunctionHandler.setDescription(toolFunction.description());
            toolFunctionHandler.setParameters(parseParameters(method));
            toolFunctionHandler.setMethod(method);
            toolFunctionHandler.setTarget(object);
            handlers.add(toolFunctionHandler);
        }
    }

    private JsonSchema parseParameters(Method method) {
        JsonSchema root = new JsonSchema();
        root.setType("object");
        root.setProperties(new LinkedHashMap<>());
        root.setRequired(new ArrayList<>());
        for (Parameter parameter : method.getParameters()) {
            ChatToolParam toolFunctionParam = ClassUtils.findAnnotation(parameter,
                ChatToolParam.class, true);
            if (toolFunctionParam == null) {
                throw new IllegalStateException(String.format("%s must be annotated with @ToolFunctionParam",
                    String.format("%s->%s", method.getName(), parameter.getName())));
            }

            String name = toolFunctionParam.name();
            JsonSchema property = parseParameter(parameter.getParameterizedType(),
                String.format("%s->%s", method.getName(), name));
            root.getProperties().put(name, property);
            property.setDescription(toolFunctionParam.description());
            if (toolFunctionParam.required()) {
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
                for (Field field : ClassUtils.getAllDeclaredFields(clazz, true)) {
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

    private Set<Method> getAllDeclaredMethods(Class<?> clazz) {
        Set<Method> allDeclaredMethods = new HashSet<>();
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            allDeclaredMethods.addAll(Arrays.asList(declaredMethods));
            clazz = clazz.getSuperclass();
        }
        return allDeclaredMethods;
    }

    private <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                    Class<A> annotationType) {
        return doFindAnnotation(annotatedElement, annotationType, new HashSet<>());
    }

    private <A extends Annotation> A doFindAnnotation(AnnotatedElement annotatedElement,
                                                      Class<A> annotationType,
                                                      Set<Class<? extends Annotation>> visited) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (visited.add(annotation.annotationType())) {
                if (annotation.annotationType() == annotationType) {
                    return annotationType.cast(annotation);
                }
                A superAnno = doFindAnnotation(annotation.annotationType(), annotationType, visited);
                if (superAnno != null) {
                    return superAnno;
                }
            }
        }
        return null;
    }

}

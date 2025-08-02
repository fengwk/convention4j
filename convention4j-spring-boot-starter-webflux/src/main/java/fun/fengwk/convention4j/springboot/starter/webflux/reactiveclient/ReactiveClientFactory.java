package fun.fengwk.convention4j.springboot.starter.webflux.reactiveclient;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.*;
import java.util.*;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;

/**
 * @author fengwk
 */
@Slf4j
public class ReactiveClientFactory {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();
    static {
        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(char.class, Character.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(void.class, Void.class);
    }

    private final WebClient.Builder webClientBuilder;

    public ReactiveClientFactory(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = Objects.requireNonNull(webClientBuilder);
    }

    public <T> T create(Class<T> reactiveClientInterface) {
        ReactiveClient reactiveFeignClient = AnnotatedElementUtils.findMergedAnnotation(reactiveClientInterface, ReactiveClient.class);
        if (reactiveFeignClient == null) {
            throw new IllegalArgumentException("reactiveClientInterface is not a ReactiveFeignClient");
        }

        Class<?> syncTarget = reactiveFeignClient.syncTarget();
        boolean syncTargetIsVoid = syncTarget == void.class;
        String url = reactiveFeignClient.url();
        String serviceId = reactiveFeignClient.value();

        String baseUrl = null;
        if (StringUtils.hasText(url)) {
            baseUrl = url;
        } else if (StringUtils.hasText(serviceId)) {
            baseUrl = "http://" + serviceId;
        }

        if (baseUrl == null) {
            throw new IllegalArgumentException("Cannot determine base url for " + reactiveClientInterface);
        }

        WebClient webClient;
        if (StringUtils.hasText(baseUrl)) {
            webClient = webClientBuilder.baseUrl(baseUrl).build();
        } else {
            webClient = webClientBuilder.build();
        }

        // 2. 预计算所有方法的执行信息
        Map<Method, ExecutionInfo> executionInfoMap = new HashMap<>();
        for (Method reactiveMethod : reactiveClientInterface.getMethods()) {
            if (reactiveMethod.isDefault()) {
                continue;
            }

            if (syncTargetIsVoid) {
                ExecutionInfo executionInfo = buildExecutionInfo(reactiveMethod, reactiveMethod);
                executionInfoMap.put(reactiveMethod, executionInfo);
            } else {
                try {
                    Method metadataMethod = syncTarget.getMethod(reactiveMethod.getName(), reactiveMethod.getParameterTypes());
                    ExecutionInfo executionInfo = buildExecutionInfo(reactiveMethod, metadataMethod);
                    executionInfoMap.put(reactiveMethod, executionInfo);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                        "Cannot find compatible method in syncTarget for: " + reactiveMethod, e);
                }
            }
        }

        // 3. 创建代理
        boolean autoWrap = reactiveFeignClient.autoHandleResultException();
        return (T) Proxy.newProxyInstance(
            reactiveClientInterface.getClassLoader(),
            new Class<?>[]{reactiveClientInterface},
            new ReactiveInvocationHandler(webClient, executionInfoMap, autoWrap)
        );
    }

    private ExecutionInfo buildExecutionInfo(Method reactiveMethod, Method metadataMethod) {
        // 1. 校验返回类型
        Class<?> reactiveReturnType = reactiveMethod.getReturnType();
        Type genericReactiveReturnType = reactiveMethod.getGenericReturnType();
        Type bodyType;

        if (!(genericReactiveReturnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Reactive method return type must be parameterized: " + reactiveMethod.getName());
        }

        if (reactiveReturnType == Mono.class) {
            bodyType = ((ParameterizedType) genericReactiveReturnType).getActualTypeArguments()[0];
        } else if (reactiveReturnType == Flux.class) {
            bodyType = ((ParameterizedType) genericReactiveReturnType).getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Unsupported return type for reactive method: " + reactiveMethod.getName());
        }

        if (reactiveMethod != metadataMethod) {
            Class<?> metadataReturnType = metadataMethod.getReturnType();
            if (Publisher.class.isAssignableFrom(metadataReturnType)) {
                throw new IllegalArgumentException("syncTarget method return type cannot be a Publisher: " + metadataMethod);
            }
            Type genericMetadataReturnType = metadataMethod.getGenericReturnType();
            boolean typesCompatible = isTypesCompatible(bodyType, genericMetadataReturnType);
            if (!typesCompatible) {
                throw new IllegalArgumentException(String.format(
                    "Return type mismatch between reactive method[%s] and syncTarget method[%s]. Reactive body: %s, Target return: %s",
                    reactiveMethod.getName(), metadataMethod.getName(), bodyType, genericMetadataReturnType));
            }
        }

        boolean bodyIsResult = false;
        Type rawBodyType = bodyType;
        if (bodyType instanceof ParameterizedType) {
            rawBodyType = ((ParameterizedType) bodyType).getRawType();
        }
        if (rawBodyType == Result.class) {
            bodyIsResult = true;
        }

        // 2. 解析RequestMapping
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(metadataMethod, RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalArgumentException("Cannot find @RequestMapping on metadata method: " + metadataMethod.getName());
        }
        RequestMethod requestMethod = requestMapping.method().length > 0 ? requestMapping.method()[0] : RequestMethod.GET;
        String path = requestMapping.value().length > 0 ? requestMapping.value()[0] : "";

        // 3. 解析参数
        List<ParamInfo> paramInfos = new ArrayList<>();
        Parameter[] parameters = metadataMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            paramInfos.add(new ParamInfo(parameters[i]));
        }

        return new ExecutionInfo(
            HttpMethod.valueOf(requestMethod.name()),
            path,
            paramInfos,
            reactiveReturnType,
            bodyType,
            bodyIsResult
        );
    }

    private boolean isTypesCompatible(Type type1, Type type2) {
        if (Objects.equals(type1, type2)) {
            return true;
        }
        if (type1 instanceof Class && type2 instanceof Class) {
            Class<?> class1 = (Class<?>) type1;
            Class<?> class2 = (Class<?>) type2;
            return Objects.equals(PRIMITIVE_WRAPPER_MAP.get(class1), class2) ||
                   Objects.equals(PRIMITIVE_WRAPPER_MAP.get(class2), class1);
        }
        return false;
    }

    private static class ReactiveInvocationHandler implements InvocationHandler {

        private final WebClient webClient;
        private final Map<Method, ExecutionInfo> executionInfoMap;
        private final boolean autoWrapResultError;

        public ReactiveInvocationHandler(WebClient webClient, Map<Method, ExecutionInfo> executionInfoMap, boolean autoWrapResultError) {
            this.webClient = webClient;
            this.executionInfoMap = executionInfoMap;
            this.autoWrapResultError = autoWrapResultError;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, args);
            }

            ExecutionInfo executionInfo = executionInfoMap.get(method);
            if (executionInfo == null) {
                return Mono.error(new UnsupportedOperationException("Unsupported method: " + method.getName()));
            }

            Map<String, Object> pathVariables = new HashMap<>();
            Map<String, Object> requestParams = new HashMap<>();
            Map<String, Object> requestHeaders = new HashMap<>();
            Object requestBody = null;

            try {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    ParamInfo paramInfo = executionInfo.getParamInfos().get(i);
                    requestBody = paramInfo.process(arg, pathVariables, requestParams, requestHeaders, requestBody);
                }
            } catch (IllegalArgumentException e) {
                if (executionInfo.getReactiveReturnType() == Mono.class) {
                    return Mono.error(e);
                } else { // Flux
                    return Flux.error(e);
                }
            }

            WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClient
                .method(executionInfo.getHttpMethod())
                .uri(uriBuilder -> {
                    uriBuilder.path(executionInfo.getPath());
                    requestParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build(pathVariables);
                });

            requestHeaders.forEach((k, v) -> requestHeadersSpec.header(k, v.toString()));

            WebClient.ResponseSpec responseSpec;
            if (requestBody != null) {
                responseSpec = ((WebClient.RequestBodySpec) requestHeadersSpec).bodyValue(requestBody).retrieve();
            } else {
                responseSpec = requestHeadersSpec.retrieve();
            }

            Class<?> declaringClass = method.getDeclaringClass();
            boolean wrapError = autoWrapResultError && executionInfo.isBodyIsResult();

            if (executionInfo.getReactiveReturnType() == Mono.class) {
                Mono<Object> mono = responseSpec.bodyToMono(ParameterizedTypeReference.forType(executionInfo.getBodyType()));
                if (wrapError) {
                    return mono.onErrorResume(err -> {
                        log.error("{}#{} error, params: {}", declaringClass.getSimpleName(), method.getName(), args, err);
                        return Mono.just(Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR.resolve(err.getMessage())));
                    });
                } else {
                    return mono.doOnError(err -> log.error("{}#{} error, params: {}",
                        declaringClass.getSimpleName(), method.getName(), args, err));
                }
            } else { // Flux
                Flux<Object> flux = responseSpec.bodyToFlux(ParameterizedTypeReference.forType(executionInfo.getBodyType()));
                if (wrapError) {
                    return flux.onErrorResume(err -> {
                        log.error("{}#{} error, params: {}", declaringClass.getSimpleName(), method.getName(), args, err);
                        return Flux.just(Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR.resolve(err.getMessage())));
                    });
                } else {
                    return flux.doOnError(err -> log.error("{}#{} error, params: {}",
                        declaringClass.getSimpleName(), method.getName(), args, err));
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class ExecutionInfo {
        private HttpMethod httpMethod;
        private String path;
        private List<ParamInfo> paramInfos;
        private Class<?> reactiveReturnType;
        private Type bodyType;
        private boolean bodyIsResult;
    }

    private static class ParamInfo {
        private final String name;
        private final boolean required;
        private final String defaultValue;
        private final ParamType type;

        ParamInfo(Parameter parameter) {
            PathVariable pathVariable = AnnotatedElementUtils.findMergedAnnotation(parameter, PathVariable.class);
            if (pathVariable != null) {
                this.type = ParamType.PATH_VARIABLE;
                String name = pathVariable.value();
                if (name.isEmpty()) {
                    name = parameter.getName();
                }
                this.name = name;
                this.required = true;
                this.defaultValue = DEFAULT_NONE;
                return;
            }

            RequestParam requestParam = AnnotatedElementUtils.findMergedAnnotation(parameter, RequestParam.class);
            if (requestParam != null) {
                this.type = ParamType.QUERY_PARAM;
                String name = requestParam.value();
                if (name.isEmpty()) {
                    name = parameter.getName();
                }
                this.name = name;
                this.required = requestParam.required();
                this.defaultValue = requestParam.defaultValue();
                return;
            }

            RequestHeader requestHeader = AnnotatedElementUtils.findMergedAnnotation(parameter, RequestHeader.class);
            if (requestHeader != null) {
                this.type = ParamType.HEADER;
                String name = requestHeader.value();
                if (name.isEmpty()) {
                    name = parameter.getName();
                }
                this.name = name;
                this.required = requestHeader.required();
                this.defaultValue = requestHeader.defaultValue();
                return;
            }

            RequestBody requestBody = AnnotatedElementUtils.findMergedAnnotation(parameter, RequestBody.class);
            if (requestBody != null) {
                this.type = ParamType.BODY;
                this.name = "";
                this.required = requestBody.required();
                this.defaultValue = DEFAULT_NONE;
                return;
            }

            // default as RequestParam
            this.type = ParamType.QUERY_PARAM;
            this.name = parameter.getName();
            this.required = true;
            this.defaultValue = DEFAULT_NONE;
        }

        Object process(Object arg, Map<String, Object> pathVariables, Map<String, Object> requestParams, Map<String, Object> requestHeaders, Object requestBody) {
            if (arg == null) {
                if (required) {
                    if (Objects.equals(DEFAULT_NONE, defaultValue)) {
                        throw new IllegalArgumentException(String.format("%s '%s' is required", type, name));
                    } else {
                        arg = defaultValue;
                    }
                }
            }

            if (arg != null) {
                switch (type) {
                    case PATH_VARIABLE: pathVariables.put(name, arg); break;
                    case QUERY_PARAM: requestParams.put(name, arg); break;
                    case HEADER: requestHeaders.put(name, arg); break;
                    case BODY:
                        if (requestBody != null) {
                            throw new IllegalArgumentException("Multiple request body is not supported");
                        }
                        requestBody = arg;
                        break;
                }
            }
            return requestBody;
        }
    }
    
    private enum ParamType {
        PATH_VARIABLE, QUERY_PARAM, HEADER, BODY
    }
}

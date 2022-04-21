package fun.fengwk.convention4j.common.i18n;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息管理器拦截器。
 * 
 * @author fengwk
 */
public class StringManagerProxyInvocationHandler implements InvocationHandler {

    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
    private static final Constructor<MethodHandles.Lookup> lookupConstructor;
    private static final Method privateLookupInMethod;
    
    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        privateLookupInMethod = privateLookupIn;

        Constructor<MethodHandles.Lookup> lookup = null;
        if (privateLookupInMethod == null) {
            // JDK 1.8
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
                        e);
            } catch (Exception e) {
                lookup = null;
            }
        }
        lookupConstructor = lookup;
    }
    
    private final StringManager stringManager;
    
    public StringManagerProxyInvocationHandler(StringManager stringManager) {
        this.stringManager = stringManager;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else if (method.isDefault()) {
            MethodHandle methodHandle;
            if (privateLookupInMethod == null) {
                methodHandle = getMethodHandleJava8(method);
            } else {
                methodHandle = getMethodHandleJava9(method);
            }
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
        } else {
            
            if (method.getReturnType() != String.class) {
                throw new IllegalStateException("Return value is not " + String.class);
            }
            
            String key = method.getName();
            Map<String, Object> ctx = new HashMap<>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Name paramName = parameter.getAnnotation(Name.class);
                if (paramName != null) {
                    ctx.put(paramName.value(), args[i]);
                } else {
                    ctx.put(parameter.getName(), args[i]);
                }
            }
            
            String str = stringManager.getString(key, ctx);
            return str;
        }
    }
    
    /* copy from mybatis MapperProxy */

    private MethodHandle getMethodHandleJava9(Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) privateLookupInMethod
                .invoke(null, declaringClass, MethodHandles.lookup()))
                .findSpecial(
                        declaringClass,
                        method.getName(),
                        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                        declaringClass);
    }

    private MethodHandle getMethodHandleJava8(Method method) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return lookupConstructor
                .newInstance(declaringClass, ALLOWED_MODES)
                .unreflectSpecial(method, declaringClass);
    }

}

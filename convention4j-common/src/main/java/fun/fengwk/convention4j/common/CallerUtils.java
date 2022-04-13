package fun.fengwk.convention4j.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * {@link CallerUtils}提供了一些获取调用者信息的方法。
 * 
 * @author fengwk
 */
public class CallerUtils {
    
    private CallerUtils() {}

    /**
     * 获取调用者Class信息。
     * 
     * @param cl
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @return 返回当前函数的调用者对象，如果无法通过传入的类加载器查找到调用类则返回null。
     */
    public static Class<?> getCallerClass(ClassLoader cl, int offset) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        StackTraceElement callerElement = elements[1 + offset];
        try {
            return Class.forName(callerElement.getClassName(), false, cl);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }
    
    /**
     * 获取调用者Class信息。
     * 
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @return 返回当前函数的调用者对象，如果无法通过默认的类加载器查找到调用类则返回null。
     */
    public static Class<?> getCallerClass(int offset) {
        return getCallerClass(ClassUtils.getDefaultClassLoader(), offset + 1);
    }
    
    /**
     * 获取调用者Method信息。
     * 
     * @param cl
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @param parameterTypes 目标调用者的方法参数列表。
     * @return 返回当前函数的调用者方法，如果无法通过传入的类加载器查找到调用类或者该类载器无权访问调用者类方法则返回null。
     */
    public static Method getCallerMethod(ClassLoader cl, int offset, Class<?>... parameterTypes) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        StackTraceElement callerElement = elements[1 + offset];
        Class<?> callerClass;
        try {
            callerClass = Class.forName(callerElement.getClassName(), false, cl);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
        
        try {
            return callerClass.getDeclaredMethod(callerElement.getMethodName(), parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    
    /**
     * 获取调用者Method信息。
     * 
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @param parameterTypes 目标调用者的方法参数列表。
     * @return 返回当前函数的调用者方法，如果无法通过默认的类加载器查找到调用类或者该类载器无权访问调用者类方法或者调用者不是一个方法则返回null。
     */
    public static Method getCallerMethod(int offset, Class<?>... parameterTypes) {
        return getCallerMethod(ClassUtils.getDefaultClassLoader(), offset + 1, parameterTypes);
    }
    
    /**
     * 获取调用者Constructor信息。
     * 
     * @param cl
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @param parameterTypes 目标调用者的方法参数列表。
     * @return 返回当前函数的调用者方法，如果无法通过传入的类加载器查找到调用类或者该类载器无权访问调用者类方法或者调用者不是一个构造器则返回null。
     */
    public static Constructor<?> getCallerConstructor(ClassLoader cl, int offset, Class<?>... parameterTypes) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        StackTraceElement callerElement = elements[1 + offset];
        Class<?> callerClass;
        try {
            callerClass = Class.forName(callerElement.getClassName(), false, cl);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
        
        try {
            return callerClass.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    
    /**
     * 获取调用者Constructor信息。
     * 
     * @param offset 偏移量，当前调用者为0，再上层调用者为1，以此类推。
     * @param parameterTypes 目标调用者的方法参数列表。
     * @return 返回当前函数的调用者方法，如果无法通过传入的类加载器查找到调用类或者该类载器无权访问调用者类方法或者调用者不是一个构造器则返回null。
     */
    public static Constructor<?> getCallerConstructor(int offset, Class<?>... parameterTypes) {
        return getCallerConstructor(ClassUtils.getDefaultClassLoader(), offset + 1, parameterTypes);
    }

}

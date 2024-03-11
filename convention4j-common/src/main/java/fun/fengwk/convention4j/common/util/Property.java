package fun.fengwk.convention4j.common.util;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * {@link Property}是对JavaBean属性的描述，一种常见的使用方式是Property允许使用代码形式获取JavaBean属性属性，
 * 这样做可以避免直接使用字符串书写JavaBean属性产生的错误。
 *
 * <pre> {@code
 *     Property cityProperty = Property.of(User::getAddr).dot(Address::getCity);
 * } </pre>
 *
 * @author fengwk
 */
public class Property<T, R> {
    
    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    private final Property<?, T> parent;
    private final SerializedLambda desc;
    
    private Class<?> rootBeanClass;
    private Class<T> beanClass;
    private String name;
    private String path;
    
    private Property(Property<?, T> parent, Fn<T, R> fn) {
        this.parent = parent;
        try {
            this.desc = parseToSerializedLambda(fn);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private SerializedLambda parseToSerializedLambda(Fn<T, R> fn) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = fn.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(fn);
    }
    
    /**
     * 获取Bean属性。
     * 
     * @param <T>
     * @param <R>
     * @param fn not null
     * @return
     */
    public static <T, R> Property<T, R> of(Fn<T, R> fn) {
        if (fn == null) {
            throw new NullPointerException("fn cannot be null");
        }

        return new Property<>(null, fn);
    }
    
    /**
     * 获取当前属性描述JavaBean的下一级属性。
     * 
     * @param <U>
     * @param fn not null
     * @return
     */
    public <U> Property<R, U> dot(Fn<R, U> fn) {
        if (fn == null) {
            throw new NullPointerException("fn cannot be null");
        }

        return new Property<>(this, fn);
    }
    
    /**
     * 检查当前属性所属的JavaBean是否为根。
     * 
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }
    
    /**
     * 获取根JavaBean的Class。
     * 
     * @return
     */
    public Class<?> getRootBeanClass() {
        if (rootBeanClass != null) {
            return rootBeanClass;
        }
        evalOriginalBeanClass();
        return rootBeanClass;
    }
    
    /**
     * 获取当前JavaBean的Class。
     * 
     * @return
     */
    public Class<T> getBeanClass() {
        if (beanClass != null) {
            return beanClass;
        }
        evalBeanClass();
        return beanClass;
    }
    
    /**
     * 获取当前属性名称。
     * 
     * @return
     */
    public String getName() {
        if (name != null) {
            return name;
        }
        evalName();
        return name;
    }
    
    /**
     * 获取从根JavaBean到当前属性路径，例如addr.city。
     * 
     * @return
     */
    public String getPath() {
        if (path != null) {
            return path;
        }
        evalPath();
        return this.path;
    }
    
    private void evalOriginalBeanClass() {
        Property<?, ?> p = this;
        while (p.parent != null) {
            p = p.parent;
        }
        rootBeanClass = p.getBeanClass();
    }
    
    @SuppressWarnings("unchecked")
    private void evalBeanClass() {
        String classPath = desc.getImplClass();
        try {
            beanClass = (Class<T>) Class.forName(classPath.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private void evalName() {
        String getter = desc.getImplMethodName();
        if (GET_PATTERN.matcher(getter).matches()) {
            getter = getter.substring(3);
        } else if (IS_PATTERN.matcher(getter).matches()) {
            getter = getter.substring(2);
        }
        name = Introspector.decapitalize(getter);
    }
    
    private void evalPath() {
        LinkedList<Property<?, ?>> stack = new LinkedList<>();
        Property<?, ?> p = this;
        while (p != null) {
            stack.push(p);
            p = p.parent;
        }
        StringJoiner joiner = new StringJoiner(".");
        while (!stack.isEmpty()) {
            joiner.add(stack.pop().getName());
        }
        path = joiner.toString();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(parent, desc);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        @SuppressWarnings("unchecked")
        Property<T, R> other = (Property<T, R>) obj;
        return Objects.equals(parent, other.parent) 
                && Objects.equals(desc, other.desc);
    }
    
    @Override
    public String toString() {
        return getPath();
    }

    @FunctionalInterface
    public interface Fn<T, R> extends Function<T, R>, Serializable {}
    
}

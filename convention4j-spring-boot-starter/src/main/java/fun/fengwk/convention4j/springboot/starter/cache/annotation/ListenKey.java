package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import fun.fengwk.convention4j.springboot.starter.cache.registry.PropertyPath;

import java.lang.annotation.*;

/**
 * {@link ListenKey}用于指定当前被注释的属性或字段和缓存对象指定字段{@link #value()}之间的监听关系。
 * 一旦缓存对象的指定字段发生了变化，就会清理对应值的指定的字段。
 * 注意：被注释的字段必须是简单类型或者是一个可迭代的简单类型，缓存对象指定的字段必须是一个简单类型，
 * 简单类型与{@link org.springframework.beans.BeanUtils#isSimpleProperty}的解释一致。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface ListenKey {

    /**
     * 指定要监听的缓存键，其值应该是一个表达式，表明要监听的键的路径。
     * @see PropertyPath
     */
    String value();

    /**
     * 当前值是否非空，使用非空值可以提高缓存清理效率，但错误的标记可能会导致缓存未失效。
     */
    boolean required() default true;

}

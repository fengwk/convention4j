package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import fun.fengwk.convention4j.springboot.starter.cache.registry.PropertyPath;

import java.lang.annotation.*;

/**
 * 该注解用于标注读方法参数中的缓存对象的主键，被标记的主键属性将使用指定的查询函数进行查询。
 * @see WriteMethod
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface EvictIndex {

    /**
     * 指定所有索引的路径，如果没有指定任何路径代表本身。
     * @see PropertyPath
     */
    String[] value() default "";

}

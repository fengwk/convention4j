package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 当该注解注释的参数或字段被修改时，将清理关联的缓存。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Key {

    /**
     * 当前属性的名称，如果没有显示指定，对于parameter将使用参数名，对于filed将使用字段名。
     */
    String value() default "";

    /**
     * 指定当前键是否为id。
     */
    boolean id() default false;

    /**
     * 如果当前键为null时将不作为执行条件应当设置为true。
     */
    boolean selective() default false;

}

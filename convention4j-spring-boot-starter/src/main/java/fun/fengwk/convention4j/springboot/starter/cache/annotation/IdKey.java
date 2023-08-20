package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解表示被注释字段为id键，当该注解注释的参数或字段被修改时，将清理关联的缓存。
 * 注意：{@link IdKey}和{@link Key}注解不能同时使用。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface IdKey {

    /**
     * 当前属性的名称，如果没有显示指定，对于parameter将使用参数名，对于filed将使用字段名。
     */
    String value() default "";

}

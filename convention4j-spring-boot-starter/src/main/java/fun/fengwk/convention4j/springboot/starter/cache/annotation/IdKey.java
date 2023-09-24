package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 该注解表示被注释字段为id键，当该注解注释的参数或字段被修改时，将清理关联的缓存。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Key(id = true)
public @interface IdKey {

    /**
     * 当前属性的名称，如果没有显示指定，对于parameter将使用参数名，对于filed将使用字段名。
     */
    @AliasFor(value = "value", annotation = Key.class)
    String value() default "";

}

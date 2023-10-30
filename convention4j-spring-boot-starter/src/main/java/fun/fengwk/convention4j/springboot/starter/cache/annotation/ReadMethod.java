package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于标注读方法。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface ReadMethod {

    /**
     * 缓存名称，默认使用被注解方法的名称。
     */
    String name() default "";

    /**
     * 缓存版本信息，通常在对当前缓存方法进行升级后失效历史缓存。
     */
    String version() default "";

}

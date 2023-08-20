package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 用于标注缓存读方法，被注释的方法在调用时将优先考虑缓存中的数据。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheReadMethod {

    /**
     * 是否可以使用id进行查询。
     */
    boolean useIdQuery() default false;

}

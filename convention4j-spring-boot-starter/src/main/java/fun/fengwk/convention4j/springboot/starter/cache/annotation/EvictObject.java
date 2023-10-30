package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于标注读方法参数中的缓存对象，被标记的对象将被作为失效对象。
 * @see WriteMethod
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface EvictObject {
}

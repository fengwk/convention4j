package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于标注写方法。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface WriteMethod {

    /**
     * 主键查询方法名称，该方法应当可以按顺序接受{@link EvictIndex#value()}指出的所有属性，
     * 若未使用{@link EvictIndex}则该参数可为空。
     */
    String objQueryMethod() default "";

}

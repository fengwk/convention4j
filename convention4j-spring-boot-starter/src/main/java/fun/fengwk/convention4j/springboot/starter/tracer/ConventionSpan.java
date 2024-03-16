package fun.fengwk.convention4j.springboot.starter.tracer;

import fun.fengwk.convention4j.tracer.util.SpanPropagation;
import io.opentracing.tag.Tags;

import java.lang.annotation.*;

/**
 * 开启一个新的span
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConventionSpan {

    /**
     * span操作名，如果为空表示使用自动生成span的操作名
     */
    String value() default "";

    /**
     * span别名，通常是更易于理解的观察的名称
     */
    String alias() default "";

    /**
     * span的分类
     */
    String kind() default Tags.SPAN_KIND_SERVER;

    /**
     * span的传播行为
     */
    SpanPropagation propagation() default SpanPropagation.SUPPORTS;

}
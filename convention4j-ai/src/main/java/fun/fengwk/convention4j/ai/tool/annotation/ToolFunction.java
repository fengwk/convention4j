package fun.fengwk.convention4j.ai.tool.annotation;

import java.lang.annotation.*;

/**
 * 工具函数
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ToolFunction {

    /**
     * 工具名称，默认为方法名
     *
     * @return 工具名称
     */
    String name() default "";

    /**
     * 获取函数描述
     *
     * @return 函数描述
     */
    String description() default "";

}

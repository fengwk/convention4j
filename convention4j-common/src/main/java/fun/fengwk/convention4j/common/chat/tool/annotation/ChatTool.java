package fun.fengwk.convention4j.common.chat.tool.annotation;

import java.lang.annotation.*;

/**
 * 工具函数
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ChatTool {

    /**
     * 获取函数描述
     *
     * @return 函数描述
     */
    String description() default "";

}

package fun.fengwk.convention4j.common.chat.tool.annotation;

import java.lang.annotation.*;

/**
 * 工具函数参数
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ChatToolParam {

    /**
     * 描述参数名称
     *
     * @return 参数名称
     */
    String name();

    /**
     * 描述参数描述
     *
     * @return 参数描述
     */
    String description() default "";

    /**
     * 描述参数是否必须
     *
     * @return 参数是否必须
     */
    boolean required() default true;

}

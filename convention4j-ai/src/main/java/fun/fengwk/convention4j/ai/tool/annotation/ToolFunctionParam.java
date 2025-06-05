package fun.fengwk.convention4j.ai.tool.annotation;

import java.lang.annotation.*;

/**
 * 工具函数参数
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ToolFunctionParam {

    /**
     * 描述参数名称，默认取参数名（注意加入编译的参数名会获取失败）
     *
     * @return 参数名称
     */
    String name() default "";

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

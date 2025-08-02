package fun.fengwk.convention4j.springboot.starter.webflux.reactiveclient;

import java.lang.annotation.*;

/**
 * @author fengwk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReactiveClient {

    /**
     * 服务id
     */
    String value() default "";

    /**
     * 服务地址
     */
    String url() default "";

    /**
     * 同步目标，用于获取元信息，如果未指定将直接解析当前接口获取元信息
     */
    Class<?> syncTarget() default void.class;

    /**
     * 是否自动处理{@link fun.fengwk.convention4j.common.result.Results}结果异常
     */
    boolean autoHandleResultException() default true;

}

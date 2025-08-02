package fun.fengwk.convention4j.springboot.starter.webflux.reactiveclient;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fengwk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ReactiveClientRegistrar.class, ReactiveClientFactory.class})
public @interface EnableReactiveClients {

    /**
     * 扫描路径
     */
    String[] basePackages() default {};

    /**
     * 指定客户端类
     */
    Class<?>[] clients() default {};

}

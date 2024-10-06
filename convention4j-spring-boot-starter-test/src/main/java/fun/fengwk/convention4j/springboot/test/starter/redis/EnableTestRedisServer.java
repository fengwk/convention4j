package fun.fengwk.convention4j.springboot.test.starter.redis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使用该注解可以引入内嵌的Redis服务器到Spring测试容器中。
 *
 * @author fengwk
 */
@Import({RedisServerTestConfig.class, RedisEagerInitBeanFactoryPostProcessor.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTestRedisServer {
}

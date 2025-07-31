package fun.fengwk.convention4j.springboot.test.starter.discovery;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 使用该注解可以引入内嵌的Redis服务器到Spring测试容器中。
 *
 * @author fengwk
 */
@EnableConfigurationProperties({TestDiscoveryProperties.class})
@Import({TestDiscoveryClient.class, TestReactiveDiscoveryClient.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTestDiscoveryClient {
}

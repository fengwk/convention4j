package fun.fengwk.convention4j.spring.cloud.starter.mock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Spring Cloud 模拟环境, 通过配置的方式模拟服务发现, 启用环境需要禁用 nacos
 * {@code spring.cloud.nacos.config.enabled=false}
 * {@code spring.cloud.nacos.discovery.enabled=false}
 *
 * @author fengwk
 * @see SpringCloudMockEnvironmentProperties
 */
@EnableConfigurationProperties({SpringCloudMockEnvironmentProperties.class})
@Import({MockDiscoveryClient.class, MockReactiveDiscoveryClient.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSpringCloudMockEnvironment {
}
package fun.fengwk.convention4j.springboot.test.starter.snowflake;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解可以引入雪花id到Spring测试容器中。
 *
 * @author fengwk
 */
@Import(SnowflakeIdConfig.class)
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTestSnowflakeId {
}

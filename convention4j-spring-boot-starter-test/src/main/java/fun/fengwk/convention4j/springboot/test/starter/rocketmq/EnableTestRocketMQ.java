package fun.fengwk.convention4j.springboot.test.starter.rocketmq;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fengwk
 */
@Import(TestRocketMQConfig.class)
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTestRocketMQ {
}

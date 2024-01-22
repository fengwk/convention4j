package fun.fengwk.convention4j.springboot.starter.scan;

import java.lang.annotation.*;

/**
 * 携带该注解的类将被排除在组件扫描之外。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcludeComponent {
}

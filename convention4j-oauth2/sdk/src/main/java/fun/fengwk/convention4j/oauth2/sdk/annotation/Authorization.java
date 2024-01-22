package fun.fengwk.convention4j.oauth2.sdk.annotation;

import java.lang.annotation.*;

/**
 * 授权注解，被标注的方法（如果标注类时为类中所有受切面影响的方法）将必须完成OAuth2授权后才能访问。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Authorization {
}

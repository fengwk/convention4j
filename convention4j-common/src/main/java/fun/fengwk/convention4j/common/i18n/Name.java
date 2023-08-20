package fun.fengwk.convention4j.common.i18n;

import java.lang.annotation.*;

/**
 * 参数名称。
 * 
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Name {

    String value();
    
}

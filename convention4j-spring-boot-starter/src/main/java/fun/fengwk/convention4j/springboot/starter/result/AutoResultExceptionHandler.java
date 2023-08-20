package fun.fengwk.convention4j.springboot.starter.result;

import java.lang.annotation.*;

/**
 * 自动Result异常处理器。
 * 在类上添加该注解将为当前类所有以Result为返回值的方法上增加切面，对于抛出的异常的情况，返回处理后的Result代替抛出的异常。
 *
 * @see ResultExceptionHandler
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoResultExceptionHandler {
}

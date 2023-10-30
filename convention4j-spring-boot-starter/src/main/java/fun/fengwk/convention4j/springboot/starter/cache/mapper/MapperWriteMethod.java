package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.WriteMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @see WriteMethod
 * @see CacheableMapper
 * @author fengwk
 */
@WriteMethod(objQueryMethod = "findForUpdateByIdIn")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MapperWriteMethod {

    /**
     * @see WriteMethod#objQueryMethod()
     */
    @AliasFor(annotation = WriteMethod.class, attribute = "objQueryMethod")
    String objQueryMethod() default "findForUpdateByIdIn";

}

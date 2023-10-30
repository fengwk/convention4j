package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.ReadMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @see ReadMethod
 * @see CacheableMapper
 * @author fengwk
 */
@ReadMethod
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MapperReadMethod {

    /**
     * @see fun.fengwk.convention4j.springboot.starter.cache.annotation.ReadMethod#name()
     */
    @AliasFor(annotation = ReadMethod.class, attribute = "name")
    String name() default "";

    /**
     * @see fun.fengwk.convention4j.springboot.starter.cache.annotation.ReadMethod#version()
     */
    @AliasFor(annotation = ReadMethod.class, attribute = "version")
    String version() default "";

}

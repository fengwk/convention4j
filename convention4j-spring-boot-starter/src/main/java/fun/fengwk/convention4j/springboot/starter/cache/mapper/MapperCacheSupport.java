package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheSupport;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.NonWriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.provider.MapperObjectClassProvider;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.provider.MapperWriteTransactionSupport;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author fengwk
 */
@CacheSupport(
    objClass = MapperObjectClassProvider.class,
    writeTransactionSupport = MapperWriteTransactionSupport.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface MapperCacheSupport {

    /**
     * @see CacheSupport#objClass()
     */
    @AliasFor(value = "objClass", annotation = CacheSupport.class)
    Class<?> objClass() default MapperObjectClassProvider.class;

    /**
     * @see CacheSupport#writeTransactionSupport()
     */
    @AliasFor(value = "writeTransactionSupport", annotation = CacheSupport.class)
    Class<? extends WriteTransactionSupport> writeTransactionSupport() default MapperWriteTransactionSupport.class;

}

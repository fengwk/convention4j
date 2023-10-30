package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.NonWriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;

import java.lang.annotation.*;

/**
 * 被当前注解注释的Spring容器对象将被支持缓存。
 *
 * @author fengwk
 * @see ReadMethod
 * @see ListenKey
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
public @interface CacheSupport {

    /**
     * 缓存版本信息，可以通过更新该信息完成缓存升级。
     */
    String version() default "";

    /**
     * 缓存过期时间/秒。
     */
    int expireSeconds() default 60 * 60 * 24;

    /**
     * 缓存对象类型，如果类型为{@link fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.ObjectClassProvider}
     * 将使用其提供的Class类型。
     */
    Class<?> objClass();

    /**
     * 事务写支持。
     */
    Class<? extends WriteTransactionSupport> writeTransactionSupport() default NonWriteTransactionSupport.class;

}

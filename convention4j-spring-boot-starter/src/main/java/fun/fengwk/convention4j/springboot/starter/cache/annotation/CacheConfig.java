package fun.fengwk.convention4j.springboot.starter.cache.annotation;

import fun.fengwk.convention4j.springboot.starter.cache.CacheSupportMetaManager;

import java.lang.annotation.*;

/**
 * 该注解用于配置缓存行为，方法配置的优先级高于类配置的优先级。
 *
 * @author fengwk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CacheConfig {

    /**
     * 缓存版本信息，可以通过更新该信息完成缓存升级。
     */
    String version() default CacheSupportMetaManager.DEFAULT_VERSION;

    /**
     * 缓存过期时间/秒。
     */
    int expireSeconds() default CacheSupportMetaManager.DEFAULT_EXPIRE_SECONDS;

}

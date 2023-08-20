package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.springboot.starter.cache.adapter.CacheAdapter;
import fun.fengwk.convention4j.springboot.starter.cache.adapter.StringRedisTemplateCacheAdapter;
import fun.fengwk.convention4j.springboot.starter.cache.adapter.TransactionCacheAdapter;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.CacheSupportMetrics;
import fun.fengwk.convention4j.springboot.starter.cache.metrics.LogCacheSupportMetrics;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author fengwk
 */
@EnableAspectJAutoProxy
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class CacheSupportAutoConfiguration {

    @ConditionalOnBean(StringRedisTemplate.class)
    @Bean
    public CacheAdapter cacheAdapter(StringRedisTemplate redisTemplate) {
        return new StringRedisTemplateCacheAdapter(redisTemplate);
    }

    @ConditionalOnBean(CacheAdapter.class)
    @Bean
    public TransactionCacheAdapter transactionCacheAdapter(CacheAdapter cacheAdapter) {
        return new TransactionCacheAdapter(cacheAdapter);
    }

    @ConditionalOnBean(CacheAdapter.class)
    @Bean
    public CacheSupportMetrics cacheSupportMetrics() {
        return new LogCacheSupportMetrics();
    }

    @ConditionalOnBean(CacheAdapter.class)
    @Bean
    public CacheSupportMethodHandler cacheSupportMethodHandler(
        @Qualifier("transactionCacheAdapter") CacheAdapter cacheAdapter, CacheSupportMetrics cacheSupportMetrics) {
        return new CacheSupportMethodHandler(cacheAdapter, cacheSupportMetrics);
    }

    @ConditionalOnBean(CacheSupportMethodHandler.class)
    @Bean
    public CacheSupportMetaManager cacheSupportMetaManager() {
        return new CacheSupportMetaManager();
    }

    @ConditionalOnBean({ CacheSupportMethodHandler.class, CacheSupportMetaManager.class })
    @Bean
    public CacheSupportMethodInterceptor cacheSupportMethodInterceptor(
            CacheSupportMethodHandler cacheManager, CacheSupportMetaManager metaInfoManager) {
        return new CacheSupportMethodInterceptor(cacheManager, metaInfoManager);
    }

    @ConditionalOnBean(CacheSupportMethodInterceptor.class)
    @Bean
    public DefaultPointcutAdvisor cacheSupportPointcutAdvisor(
            CacheSupportMethodInterceptor cacheSupportMethodInterceptor){
        DefaultPointcutAdvisor cacheSupportPointcutAdvisor = new DefaultPointcutAdvisor();
        cacheSupportPointcutAdvisor.setPointcut(new CacheSupportPointcut());
        cacheSupportPointcutAdvisor.setAdvice(cacheSupportMethodInterceptor);
        return cacheSupportPointcutAdvisor;
    }

}

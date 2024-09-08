package fun.fengwk.convention4j.springboot.starter.cache;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.common.cache.facade.StringRedisTemplateCacheFacade;
import fun.fengwk.convention4j.common.cache.facade.TransactionCacheFacade;
import fun.fengwk.convention4j.common.cache.metrics.CacheFacadeMetrics;
import fun.fengwk.convention4j.common.cache.metrics.CacheManagerMetrics;
import fun.fengwk.convention4j.common.cache.metrics.LogCacheManagerMetrics;
import fun.fengwk.convention4j.springboot.starter.cache.registry.DefaultCacheManagerRegistry;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

/**
 * @author fengwk
 */
@ConditionalOnClass({ RedisOperations.class, TransactionManagementConfigurationSelector.class })
@EnableTransactionManagement
@EnableAspectJAutoProxy
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class CacheSupportAutoConfiguration {

    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(CacheFacade.class)
    @Bean
    public CacheFacade cacheFacade(StringRedisTemplate redisTemplate) {
        return new StringRedisTemplateCacheFacade(redisTemplate);
    }

    @ConditionalOnBean(CacheFacade.class)
    @Bean
    public CacheFacadeMetrics cacheFacadeMetrics(@Qualifier("cacheFacade") CacheFacade cacheFacade) {
        return new CacheFacadeMetrics(cacheFacade);
    }

    @ConditionalOnBean(CacheFacadeMetrics.class)
    @Bean
    public TransactionCacheFacade transactionCacheFacade(CacheFacadeMetrics cacheFacadeMetrics) {
        return new TransactionCacheFacade(cacheFacadeMetrics);
    }

    @ConditionalOnBean(CacheFacade.class)
    @ConditionalOnMissingBean(CacheManagerMetrics.class)
    @Bean
    public CacheManagerMetrics cacheManagerMetrics() {
        return new LogCacheManagerMetrics();
    }

    @ConditionalOnBean({ CacheFacade.class, CacheManagerMetrics.class })
    @Bean
    public DefaultCacheManagerRegistry defaultCacheManagerRegistry(BeanFactory beanFactory,
        @Qualifier("transactionCacheFacade") CacheFacade cacheFacade, CacheManagerMetrics cacheManagerMetrics) {
        return new DefaultCacheManagerRegistry(beanFactory, cacheFacade, cacheManagerMetrics);
    }

    @ConditionalOnBean(DefaultCacheManagerRegistry.class)
    @Bean
    public CacheSupportMethodInterceptor cacheSupportMethodInterceptor(
        DefaultCacheManagerRegistry defaultCacheManagerRegistry) {
        return new CacheSupportMethodInterceptor(defaultCacheManagerRegistry);
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

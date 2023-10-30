package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.springboot.starter.cache.CacheSupportAutoConfiguration;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.NonWriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.provider.MapperWriteTransactionSupport;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author fengwk
 */
@ConditionalOnBean(CacheFacade.class)
@EnableTransactionManagement
@AutoConfigureAfter(CacheSupportAutoConfiguration.class)
@Configuration
public class MapperCacheAutoConfiguration {

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public NonWriteTransactionSupport nonWriteTransactionSupport() {
        return new NonWriteTransactionSupport();
    }

    @Bean
    public MapperWriteTransactionSupport mapperWriteTransactionSupport(TransactionTemplate transactionTemplate) {
        return new MapperWriteTransactionSupport(transactionTemplate);
    }

}

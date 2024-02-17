package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.springboot.starter.cache.CacheSupportAutoConfiguration;
import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.NonWriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.provider.MapperWriteTransactionSupport;
import fun.fengwk.convention4j.springboot.starter.transaction.TransactionExecutor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@ConditionalOnBean(CacheFacade.class)
@AutoConfigureAfter(CacheSupportAutoConfiguration.class)
@Configuration
public class MapperCacheAutoConfiguration {

    @Bean
    public NonWriteTransactionSupport nonWriteTransactionSupport() {
        return new NonWriteTransactionSupport();
    }

    @Bean
    public MapperWriteTransactionSupport mapperWriteTransactionSupport(TransactionExecutor transactionExecutor) {
        return new MapperWriteTransactionSupport(transactionExecutor);
    }

}

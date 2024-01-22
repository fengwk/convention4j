package fun.fengwk.convention4j.springboot.starter.transaction;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

/**
 * @author fengwk
 */
@ConditionalOnClass(TransactionManagementConfigurationSelector.class)
@EnableTransactionManagement
@Configuration
public class TransactionExecutorAutoConfiguration {

    @Bean
    public TransactionExecutor transactionExecutor() {
        return new TransactionExecutor();
    }

}

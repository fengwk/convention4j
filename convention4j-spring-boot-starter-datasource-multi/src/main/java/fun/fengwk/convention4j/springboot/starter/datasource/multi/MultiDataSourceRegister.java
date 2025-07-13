package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 * 多数据源注册器
 *
 * @author fengwk
 * @see MultiDataSourceProperties
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MultiDataSourceRegister extends AbstractMultiDataSourceRegister {

    @Override
    protected void register(BeanDefinitionRegistry registry, String name, DataSourceBeanConfig config) {
        // @see DataSourceAutoConfiguration
        BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceFactoryBean.class);
        dataSourceBuilder.addPropertyValue("name", name);
        dataSourceBuilder.addPropertyValue("config", config);
        if (Objects.equals(config.getPrimary(), Boolean.TRUE)) {
            dataSourceBuilder.setPrimary(true);
        }
        String dataSourceName = MultiDataSourceUtils.buildDataSourceName(name);
        registry.registerBeanDefinition(dataSourceName, dataSourceBuilder.getBeanDefinition());

        // @see DataSourceTransactionManagerAutoConfiguration
        BeanDefinitionBuilder transactionManagerBuilder = BeanDefinitionBuilder.genericBeanDefinition(JdbcTransactionManager.class);
        transactionManagerBuilder.addConstructorArgReference(dataSourceName);
        if (Objects.equals(config.getPrimary(), Boolean.TRUE)) {
            transactionManagerBuilder.setPrimary(true);
        }
        String transactionManagerName = MultiDataSourceUtils.buildTransactionManagerName(name);
        registry.registerBeanDefinition(transactionManagerName, transactionManagerBuilder.getBeanDefinition());

        // @see TransactionTemplateConfiguration
        BeanDefinitionBuilder transactionTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(TransactionTemplate.class);
        transactionTemplateBuilder.addConstructorArgReference(transactionManagerName);
        if (Objects.equals(config.getPrimary(), Boolean.TRUE)) {
            transactionTemplateBuilder.setPrimary(true);
        }
        String transactionTemplateName = MultiDataSourceUtils.buildTransactionTemplateName(name);
        registry.registerBeanDefinition(transactionTemplateName, transactionTemplateBuilder.getBeanDefinition());
    }

}

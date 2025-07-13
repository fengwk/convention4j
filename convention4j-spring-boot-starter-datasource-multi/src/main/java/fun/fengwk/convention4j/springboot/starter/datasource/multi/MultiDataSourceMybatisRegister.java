package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Objects;

/**
 * 多数据源Mybatis支持
 *
 * @author fengwk
 * @see MultiDataSourceProperties
 */
@Slf4j
public class MultiDataSourceMybatisRegister extends AbstractMultiDataSourceRegister {

    @Override
    protected void register(BeanDefinitionRegistry registry, String name, DataSourceBeanConfig config) {
        String dataSourceName = MultiDataSourceUtils.buildDataSourceName(name);

        // @see MybatisAutoConfiguration

        // SqlSessionFactory
        BeanDefinitionBuilder sqlSessionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(
            SqlSessionFactoryFactoryBean.class);
        sqlSessionFactoryBuilder.addAutowiredProperty("mybatisBeanFactory");
        sqlSessionFactoryBuilder.addPropertyReference("dataSource", dataSourceName);
        if (Objects.equals(config.getPrimary(), Boolean.TRUE)) {
            sqlSessionFactoryBuilder.setPrimary(true);
        }
        String sqlSessionFactoryName = MultiDataSourceUtils.buildSqlSessionFactoryName(name);
        registry.registerBeanDefinition(sqlSessionFactoryName, sqlSessionFactoryBuilder.getBeanDefinition());

        // SqlSessionTemplate
        BeanDefinitionBuilder sqlSessionTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(
            SqlSessionTemplateFactoryBean.class);
        sqlSessionTemplateBuilder.addAutowiredProperty("mybatisBeanFactory");
        sqlSessionTemplateBuilder.addPropertyReference("sqlSessionFactory", sqlSessionFactoryName);
        if (Objects.equals(config.getPrimary(), Boolean.TRUE)) {
            sqlSessionTemplateBuilder.setPrimary(true);
        }
        String sqlSessionTemplateName = MultiDataSourceUtils.buildSqlSessionTemplateName(name);
        registry.registerBeanDefinition(sqlSessionTemplateName, sqlSessionTemplateBuilder.getBeanDefinition());
    }

}

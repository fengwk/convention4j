package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

/**
 * @author fengwk
 */
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@Import({MultiDataSourceMybatisRegister.class, SqlInitializationAutoConfiguration.class})
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfiguration(after = MultiDataSourceAutoConfiguration.class)
public class MultiDataSourceMybatisAutoConfiguration {

    @Bean
    public MybatisBeanFactory mybatisBeanFactory(MybatisProperties properties,
                                                 ObjectProvider<Interceptor[]> interceptorsProvider,
                                                 ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                                 ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                                 ResourceLoader resourceLoader,
                                                 ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                                 ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                                 ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers) {
        return new MybatisBeanFactory(properties, interceptorsProvider, typeHandlersProvider,
            languageDriversProvider, resourceLoader, databaseIdProvider,
            configurationCustomizersProvider, sqlSessionFactoryBeanCustomizers);

    }

}

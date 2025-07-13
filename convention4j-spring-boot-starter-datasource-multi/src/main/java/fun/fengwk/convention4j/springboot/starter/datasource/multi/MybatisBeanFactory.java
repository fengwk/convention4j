package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;

/**
 * 用于适配{@link MultiDataSourceMybatisAutoConfiguration}
 *
 * @author fengwk
 * @see MybatisAutoConfiguration
 */
public class MybatisBeanFactory {

    private final MybatisAutoConfiguration delegate;

    public MybatisBeanFactory(MybatisProperties properties,
                              ObjectProvider<Interceptor[]> interceptorsProvider,
                              ObjectProvider<TypeHandler[]> typeHandlersProvider,
                              ObjectProvider<LanguageDriver[]> languageDriversProvider,
                              ResourceLoader resourceLoader,
                              ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                              ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                              ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers) {
        this.delegate = new MybatisAutoConfiguration(properties, interceptorsProvider,
            typeHandlersProvider, languageDriversProvider, resourceLoader, databaseIdProvider,
            configurationCustomizersProvider, sqlSessionFactoryBeanCustomizers);
    }

    public SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        return delegate.sqlSessionFactory(dataSource);
    }

    public SqlSessionTemplate createSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return delegate.sqlSessionTemplate(sqlSessionFactory);
    }

}

package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

/**
 * @author fengwk
 */
public class SqlSessionFactoryFactoryBean implements FactoryBean<SqlSessionFactory> {

    @Setter
    private MybatisBeanFactory mybatisBeanFactory;
    @Setter
    private DataSource dataSource;

    @Override
    public SqlSessionFactory getObject() throws Exception {
        return mybatisBeanFactory.createSqlSessionFactory(dataSource);
    }

    @Override
    public Class<?> getObjectType() {
        return SqlSessionFactory.class;
    }

}

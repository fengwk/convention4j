package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author fengwk
 */
public class SqlSessionTemplateFactoryBean implements FactoryBean<SqlSessionTemplate> {

    @Setter
    private MybatisBeanFactory mybatisBeanFactory;
    @Setter
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public SqlSessionTemplate getObject() {
        return mybatisBeanFactory.createSqlSessionTemplate(sqlSessionFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return SqlSessionTemplate.class;
    }

}

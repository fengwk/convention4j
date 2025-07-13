package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds2;

import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@MapperScan(markerInterface = BaseMapper.class,
    // 需要配置当前扫描目录下要使用的sqlSessionFactory和sqlSessionTemplate
    sqlSessionFactoryRef = "ds2SqlSessionFactory",
    sqlSessionTemplateRef = "ds2SqlSessionTemplate")
@Configuration
public class Ds2Configuration {
}

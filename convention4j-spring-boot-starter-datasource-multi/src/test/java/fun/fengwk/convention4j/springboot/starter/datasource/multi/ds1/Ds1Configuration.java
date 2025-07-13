package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1;

import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengwk
 */
@MapperScan(markerInterface = BaseMapper.class) // ds1为primary无需配置
@Configuration
public class Ds1Configuration {
}

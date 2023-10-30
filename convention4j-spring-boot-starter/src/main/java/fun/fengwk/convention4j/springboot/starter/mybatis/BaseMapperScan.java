package fun.fengwk.convention4j.springboot.starter.mybatis;

import org.mybatis.spring.annotation.MapperScan;

import java.lang.annotation.*;

/**
 * @author fengwk
 */
@MapperScan(markerInterface = BaseMapper.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BaseMapperScan {
}

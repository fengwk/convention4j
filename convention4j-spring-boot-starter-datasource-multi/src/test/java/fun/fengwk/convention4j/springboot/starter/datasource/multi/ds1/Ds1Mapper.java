package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;

/**
 * @author fengwk
 */
@AutoMapper
public interface Ds1Mapper extends BaseMapper {

    int insert(Ds1DO ds1DO);

    Ds1DO getByName(String name);

}

package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds2;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;

/**
 * @author fengwk
 */
@AutoMapper
public interface Ds2Mapper extends BaseMapper {

    int insert(Ds2DO ds2DO);

    Ds2DO getByName(String name);

}

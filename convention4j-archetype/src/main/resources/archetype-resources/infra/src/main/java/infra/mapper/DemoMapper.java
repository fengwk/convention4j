#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infra.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.mybatis.BaseMapper;
import ${package}.infra.model.DemoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface DemoMapper extends BaseMapper {

    void createTableIfNotExists();

    int insertSelective(DemoDO demoDO);

    int deleteById(long id);

    long countAll();

    List<DemoDO> pageAll(@Param("offset") long offset, @Param("limit") int limit);

}

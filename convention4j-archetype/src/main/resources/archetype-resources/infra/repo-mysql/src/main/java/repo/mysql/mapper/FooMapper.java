#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.LongIdCacheMapper;
import ${package}.repo.mysql.model.FooPO;

/**
 * @author fengwk
 */
@AutoMapper
public interface FooMapper extends LongIdCacheMapper<FooPO> {

    int insert(FooPO fooPO);

    FooPO findById(String id);

}

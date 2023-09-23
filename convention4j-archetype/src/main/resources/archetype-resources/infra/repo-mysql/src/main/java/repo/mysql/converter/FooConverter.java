#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql.converter;

import ${package}.domain.model.FooBO;
import ${package}.repo.mysql.model.FooPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author fengwk
 */
@Mapper
public interface FooConverter {

    FooConverter INSTANCE = Mappers.getMapper(FooConverter.class);

    FooPO convert(FooBO fooBO);

    FooBO convert(FooPO fooPO);

}

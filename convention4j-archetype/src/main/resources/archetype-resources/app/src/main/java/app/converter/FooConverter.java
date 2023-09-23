#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.converter;

import ${package}.domain.model.FooBO;
import ${package}.domain.model.FooCreateBO;
import ${package}.share.model.FooCreateDTO;
import ${package}.share.model.FooDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author fengwk
 */
@Mapper
public interface FooConverter {

    FooConverter INSTANCE = Mappers.getMapper(FooConverter.class);

    FooCreateBO convert(FooCreateDTO createDTO);

    FooDTO convert(FooBO fooBO);

}

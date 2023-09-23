#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.repo;

import ${package}.domain.model.FooBO;

/**
 * @author fengwk
 */
public interface FooRepository {

    String allocateId();

    boolean insert(FooBO fooBO);

    FooBO getById(String id);

}

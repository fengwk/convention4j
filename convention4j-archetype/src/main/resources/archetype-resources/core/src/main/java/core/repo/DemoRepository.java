#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import ${package}.core.model.Demo;

/**
 * @author fengwk
 */
public interface DemoRepository {

    void init();

    long generateId();

    boolean add(Demo demo);

    boolean removeById(long id);

    Page<Demo> page(PageQuery pageQuery);

}

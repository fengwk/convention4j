#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import ${package}.share.model.DemoCreateDTO;
import ${package}.share.model.DemoDTO;

/**
 * @author fengwk
 */
public interface DemoService {

    DemoDTO createDemo(DemoCreateDTO createDTO);

    void removeDemo(long id);

    Page<DemoDTO> pageDemo(PageQuery pageQuery);

}
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql.model;

import fun.fengwk.convention4j.springboot.starter.cache.mapper.BaseCachePO;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class FooPO extends BaseCachePO<Long> {

    private String name;
    private String status;

}

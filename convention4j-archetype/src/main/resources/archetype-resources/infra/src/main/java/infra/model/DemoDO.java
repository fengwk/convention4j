#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infra.model;

import fun.fengwk.convention4j.springboot.starter.persistence.ConventionDO;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class DemoDO extends ConventionDO<Long> {

    private String name;

}

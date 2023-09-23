#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.model;

import ${package}.share.constant.FooStatus;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class FooCreateBO {

    private String name;
    private FooStatus status;

}

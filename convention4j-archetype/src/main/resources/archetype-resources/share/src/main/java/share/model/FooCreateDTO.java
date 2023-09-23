#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.share.model;

import ${package}.share.constant.FooStatus;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class FooCreateDTO {

    private String name;

    /**
     * @see FooStatus
     */
    private String status;

}

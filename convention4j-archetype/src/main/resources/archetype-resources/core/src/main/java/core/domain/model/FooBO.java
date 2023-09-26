#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.domain.model;

import ${package}.share.constant.FooStatus;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class FooBO {

    private String id;
    private String name;
    private FooStatus status;

}

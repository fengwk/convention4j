#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.share.model;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class DemoUpdateDTO extends DemoEditablePropertiesDTO {

    private long id;

}

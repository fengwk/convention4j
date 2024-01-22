#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.share.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
@Data
public class DemoDTO {

    private long id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}

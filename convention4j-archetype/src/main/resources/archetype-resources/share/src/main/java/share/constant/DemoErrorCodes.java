#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.share.constant;

import fun.fengwk.convention4j.api.code.DomainConventionErrorCodeEnumAdapter;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum DemoErrorCodes implements DomainConventionErrorCodeEnumAdapter {

    CREATE_DEMO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    REMOVE_DEMO_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "DEMO";
    }

}

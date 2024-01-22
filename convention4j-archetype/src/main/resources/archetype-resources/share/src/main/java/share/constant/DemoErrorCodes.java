#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.share.constant;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum DemoErrorCodes implements ConventionErrorCode {

    CREATE_DEMO_FAILED(1001, HttpStatus.INTERNAL_SERVER_ERROR),
    REMOVE_DEMO_FAILED(1002, HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final int domainCode;
    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "DEMO";
    }

    @Override
    public String getMessage() {
        return name();
    }

}

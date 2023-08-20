package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum ExampleErrorCodes implements ConventionErrorCode {

    EXAMPLE_ERROR(1, HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int domainCode;
    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "Example";
    }

}

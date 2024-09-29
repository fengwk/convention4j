package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.DomainConventionErrorCodeEnumAdapter;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum ExampleErrorCodes implements DomainConventionErrorCodeEnumAdapter {

    EXAMPLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "Example";
    }

}

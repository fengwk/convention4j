package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum TestErrorCodes implements ConventionErrorCode {

    TEST(1, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int domainCode;
    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "TEST";
    }

}

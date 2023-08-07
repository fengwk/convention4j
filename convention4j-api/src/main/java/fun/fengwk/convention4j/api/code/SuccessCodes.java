package fun.fengwk.convention4j.api.code;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.code.Status;
import lombok.AllArgsConstructor;

/**
 * 成功状态码。
 *
 * @author fengwk
 */
@AllArgsConstructor
public enum SuccessCodes implements Status {

    OK(HttpStatus.OK),
    CREATED(HttpStatus.CREATED),
    ACCEPTED(HttpStatus.ACCEPTED),
    NON_AUTHORITATIVE_INFORMATION(HttpStatus.NON_AUTHORITATIVE_INFORMATION),
    NO_CONTENT(HttpStatus.NO_CONTENT),
    RESET_CONTENT(HttpStatus.RESET_CONTENT),
    PARTIAL_CONTENT(HttpStatus.PARTIAL_CONTENT),
    MULTI_STATUS(HttpStatus.MULTI_STATUS),
    ALREADY_REPORTED(HttpStatus.ALREADY_REPORTED),
    IM_USED(HttpStatus.IM_USED),
    ;

    private final HttpStatus httpStatus;

    @Override
    public int getStatus() {
        return httpStatus.getStatus();
    }

    @Override
    public String getMessage() {
        return httpStatus.getMessage();
    }

}

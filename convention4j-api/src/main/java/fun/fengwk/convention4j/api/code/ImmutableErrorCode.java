package fun.fengwk.convention4j.api.code;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ImmutableErrorCode implements ErrorCode {

    private final int status;
    private final String code;
    private final String message;

}

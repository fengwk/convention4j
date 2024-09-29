package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ImmutableConventionCode implements ResolvedConventionCode {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String code;
    private final String message;

    public ImmutableConventionCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

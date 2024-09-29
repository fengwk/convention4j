package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ThrowableConventionErrorCode extends ThrowableErrorCode implements ConventionErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    public ThrowableConventionErrorCode(ResolvedConventionErrorCode resolvedErrorCode) {
        super(resolvedErrorCode);
    }

    public ThrowableConventionErrorCode(ResolvedConventionErrorCode resolvedErrorCode, Throwable cause) {
        super(resolvedErrorCode, cause);
    }

    @Override
    public int getStatus() {
        return ((ResolvedConventionErrorCode) getErrorCode()).getStatus();
    }

}

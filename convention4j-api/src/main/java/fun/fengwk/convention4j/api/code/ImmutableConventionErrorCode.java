package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Collections;
import java.util.Map;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ImmutableConventionErrorCode extends ImmutableConventionCode implements ConventionErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Object> errorContext;

    public ImmutableConventionErrorCode(int status, String code, String message, Map<String, Object> errorContext) {
        super(status, code, message);
        this.errorContext = errorContext == null ? Collections.emptyMap() : errorContext;
    }

    @Override
    public Map<String, Object> getErrorContext() {
        return errorContext;
    }

}

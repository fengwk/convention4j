package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Map;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ImmutableResolvedConventionErrorCode extends ImmutableConventionErrorCode implements ResolvedConventionErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImmutableResolvedConventionErrorCode(int status, String code, String message, Map<String, Object> errorContext) {
        super(status, code, message, errorContext);
    }

}

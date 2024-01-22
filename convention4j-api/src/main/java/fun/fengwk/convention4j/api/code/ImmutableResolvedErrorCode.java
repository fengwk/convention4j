package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Map;

/**
 * @author fengwk
 */
@ToString
@EqualsAndHashCode
public class ImmutableResolvedErrorCode extends ImmutableErrorCode implements ResolvedErrorCode {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImmutableResolvedErrorCode(int status, String code, String message, Map<String, Object> errorContext) {
        super(status, code, message, errorContext);
    }

}

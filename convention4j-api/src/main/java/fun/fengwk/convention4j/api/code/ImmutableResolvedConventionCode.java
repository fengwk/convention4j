package fun.fengwk.convention4j.api.code;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * @author fengwk
 */
@EqualsAndHashCode
@ToString
public class ImmutableResolvedConventionCode extends ImmutableConventionCode implements ResolvedConventionCode {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImmutableResolvedConventionCode(int status, String code, String message) {
        super(status, code, message);
    }

}

package fun.fengwk.convention4j.api.result;

import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.Data;

/**
 * 
 * @author fengwk
 */
@Data
public class DefaultResult<T> implements Result<T> {
    
    private static final long serialVersionUID = 1L;

    private final int status;
    private final String message;
    private final T data;
    private final Errors errors;

    @Override
    public boolean isSuccess() {
        return HttpStatus.is2xx(status);
    }

}
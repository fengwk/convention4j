package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeMessageResolver;
import fun.fengwk.convention4j.common.expression.ExpressionUtils;

/**
 * @author fengwk
 */
public abstract class AbstractErrorCodeMessageResolver implements ErrorCodeMessageResolver {

    @Override
    public String resolveMessage(ErrorCode errorCode, Object ctx) {
        String message = resolveMessage(errorCode);
        return ExpressionUtils.format(message, ctx);
    }

}

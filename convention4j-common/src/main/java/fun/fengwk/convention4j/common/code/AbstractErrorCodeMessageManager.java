package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCodeMessageManager;
import fun.fengwk.convention4j.common.expression.ExpressionUtils;

/**
 * @author fengwk
 */
public abstract class AbstractErrorCodeMessageManager implements ErrorCodeMessageManager {

    @Override
    public String formatMessage(String message, Object ctx) {
        return ExpressionUtils.format(message, ctx);
    }

}

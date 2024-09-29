package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.Code;
import fun.fengwk.convention4j.api.code.CodeMessageResolver;
import fun.fengwk.convention4j.common.expression.ExpressionUtils;

/**
 * @author fengwk
 */
public abstract class AbstractCodeMessageResolver implements CodeMessageResolver {

    @Override
    public String resolveMessage(Code code, Object context) {
        String message = resolveMessage(code);
        return ExpressionUtils.format(message, context);
    }

}

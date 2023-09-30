package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.code.ThrowableErrorCode;

import java.util.Map;

/**
 * @author fengwk
 */
public class ValidationException extends ThrowableErrorCode {

    public ValidationException(ValidationMessageTemplate template) {
        this(template.getTemplate());
    }

    public ValidationException(ValidationMessageTemplate template, Map<String, Object> errorContext) {
        this(template.getTemplate(), errorContext);
    }

    public ValidationException(ValidationMessageTemplate template, Throwable cause) {
        this(template.getTemplate(), cause);
    }

    public ValidationException(ValidationMessageTemplate template, Map<String, Object> errorContext, Throwable cause) {
        this(template.getTemplate(), errorContext, cause);
    }

    public ValidationException(String messageTemplate) {
        super(CommonErrorCodes.BAD_REQUEST.create(messageTemplate));
    }

    public ValidationException(String messageTemplate, Map<String, Object> errorContext) {
        super(CommonErrorCodes.BAD_REQUEST.createWithErrorContext(messageTemplate, errorContext));
    }

    public ValidationException(String messageTemplate, Throwable cause) {
        super(CommonErrorCodes.BAD_REQUEST.create(messageTemplate), cause);
    }

    public ValidationException(String messageTemplate, Map<String, Object> errorContext, Throwable cause) {
        super(CommonErrorCodes.BAD_REQUEST.createWithErrorContext(messageTemplate, errorContext), cause);
    }

}

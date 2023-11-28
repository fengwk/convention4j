package fun.fengwk.convention4j.common.validation;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.Set;


/**
 * @author fengwk
 */
public class ValidationUtils {

    private ValidationUtils() {}

    public static void addMessageParameter(ConstraintValidatorContext context, String name, Object value) {
        if (context instanceof HibernateConstraintValidatorContext) {
            HibernateConstraintValidatorContext hibernateCtx = context.unwrap(
                HibernateConstraintValidatorContext.class);
            hibernateCtx.addMessageParameter(name, value);
        }
    }

    /**
     * 检查检验结果，如果校验结果为失败则抛出异常
     * @param validateResult
     * @param <T>
     * @throws ConstraintViolationException
     */
    public static <T> void checkValidateResult(Set<ConstraintViolation<T>> validateResult)
        throws ConstraintViolationException {
        if (validateResult != null && !validateResult.isEmpty()) {
            throw new ConstraintViolationException(validateResult);
        }
    }

}

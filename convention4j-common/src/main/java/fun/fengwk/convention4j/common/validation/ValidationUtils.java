package fun.fengwk.convention4j.common.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidatorContext;

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

}

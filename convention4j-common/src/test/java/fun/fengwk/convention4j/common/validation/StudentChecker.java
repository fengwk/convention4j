package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.api.code.ThrowableErrorCode;

/**
 * @author fengwk
 */
@AutoService(ConventionChecker.class)
public class StudentChecker implements ConventionChecker<Student> {

    @Override
    public void check(Student student) {
        if (student == null) {
            throw new ValidationException(ValidationMessageTemplate.NOT_NULL);
        }
        if (student.getId() == null) {
            throw new ValidationException(ValidationMessageTemplate.NOT_NULL);
        }
        if (student.getName() == null) {
            throw new ValidationException(ValidationMessageTemplate.NOT_EMPTY);
        }
    }

}

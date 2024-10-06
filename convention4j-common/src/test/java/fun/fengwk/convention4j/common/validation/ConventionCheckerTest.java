package fun.fengwk.convention4j.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author fengwk
 */
public class ConventionCheckerTest {

    private Validator validator;

    @BeforeEach
    public void init() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        validatorFactory.close();
    }

    @Test
    public void test() {
        Student stu = new Student();
        stu.setId(123L);
        stu.setName("fengwk");
        Set<ConstraintViolation<Student>> result = validator.validate(stu);
        assert result.size() == 1;
    }

}

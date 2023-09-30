package fun.fengwk.convention4j.common.validation;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author fengwk
 */
public class ConventionCheckerTest {

    private Validator validator;

    @Before
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

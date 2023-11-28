package fun.fengwk.convention4j.common.validation.group;

import fun.fengwk.convention4j.api.validation.group.FullUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * @author fengwk
 */
public class GroupValidationTest {

    private Validator validator;

    @Before
    public void init() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        validatorFactory.close();
    }

    @Test
    public void test() {
        User user = new User();
        user.setDesc("xx");
        Set<ConstraintViolation<User>> set = validator.validate(user, Default.class, FullUpdate.class);
        assert set.size() == 2;
    }

}

package fun.fengwk.convention4j.common.validation.group;

import fun.fengwk.convention4j.api.validation.group.FullUpdate;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
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

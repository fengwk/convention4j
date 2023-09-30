package fun.fengwk.convention4j.common.validation;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author fengwk
 */
public class EnumNameTest {

    private Validator validator;

    @Before
    public void init() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        validatorFactory.close();
    }

    @Test
    public void test1() {
        TestBean th = new TestBean();
        th.setType(TestType.t1.name());
        th.setTypeList(Arrays.asList(TestType.t1.name(), TestType.t2.name()));
        th.setTypeSet(new HashSet<>(Arrays.asList(TestType.t1.name(), TestType.t2.name(), TestType.t3.name())));
        Set<ConstraintViolation<TestBean>> result = validator.validate(th);
        assert result.isEmpty();
    }

    @Test
    public void test2() {
        TestBean th = new TestBean();
        th.setType("t0");
        th.setTypeList(Arrays.asList(TestType.t1.name(), TestType.t2.name()));
        th.setTypeSet(new HashSet<>(Arrays.asList(TestType.t1.name(), TestType.t2.name(), TestType.t3.name())));
        Set<ConstraintViolation<TestBean>> result = validator.validate(th);
        assert result.size() == 1;
        String message = result.iterator().next().getMessage();
        assert "必须是't1,t2,t3'之一".equals(message) || "must be one of 't1,t2,t3'".equals(message);
    }

    @Test
    public void test3() {
        TestBean th = new TestBean();
        th.setType(TestType.t1.name());
        th.setTypeList(Arrays.asList("t0", TestType.t2.name()));
        th.setTypeSet(new HashSet<>(Arrays.asList(TestType.t1.name(), TestType.t2.name(), TestType.t3.name())));
        Set<ConstraintViolation<TestBean>> result = validator.validate(th);
        assert result.size() == 1;
        String message = result.iterator().next().getMessage();
        assert "必须是't1,t2,t3'之一".equals(message) || "must be one of 't1,t2,t3'".equals(message);
    }

}

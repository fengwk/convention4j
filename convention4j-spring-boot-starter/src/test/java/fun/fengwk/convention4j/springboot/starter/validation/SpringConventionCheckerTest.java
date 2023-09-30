package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)

public class SpringConventionCheckerTest {

    @Autowired
    private Validator validator;

    @Test
    public void test() {
        Teach teach = new Teach();
        teach.setName("fengwk");
        Set<ConstraintViolation<Teach>> result = validator.validate(teach);
        assert result.size() == 1;
        String message = result.iterator().next().getMessage();
        assert "teach.id cannot be null".equals(message);
    }

}

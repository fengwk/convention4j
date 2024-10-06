package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

/**
 * @author fengwk
 */
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

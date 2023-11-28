package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import jakarta.validation.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test(expected = ConstraintViolationException.class)
    public void test() {
        User user = new User();
        user.setId(1L);
//        user.setName("name");
        user.setDesc("desc");
        userService.checkUser(user);
    }

}

package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.springboot.starter.TestApplication;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fengwk
 */
@SpringBootTest(classes = TestApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        assertThrows(ConstraintViolationException.class, () -> {
            User user = new User();
            user.setId(1L);
//        user.setName("name");
            user.setDesc("desc");
            userService.checkUser(user);
        });
    }

}

package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.autovalidation.validator.GlobalValidator;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

/**
 * @author fengwk
 */
public class ValidatorTest {

    interface UserManager {

        void checkUsername(@NotEmpty String username);

    }

    static class UserManagerImpl implements UserManager {

        @Override
        public void checkUsername(String username) {
            GlobalValidator.checkMethodParameters(
                    UserManagerImpl.class,
                    "checkUsername",
                    new Class[] { String.class },
                    this,
                    new Object[] { username }
                    );
        }

    }

    @Test
    public void test1() {
        UserManager userManager = new UserManagerImpl();
        userManager.checkUsername("123");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test2() {
        UserManager userManager = new UserManagerImpl();
        userManager.checkUsername(null);
    }

    static abstract class AbsUserManager {

        public abstract void checkUsername(@NotEmpty String username);

    }

    static class AbsUserManagerImpl extends AbsUserManager {

        @Override
        public void checkUsername(String username) {
            GlobalValidator.checkMethodParameters(
                    AbsUserManagerImpl.class,
                    "checkUsername",
                    new Class[] { String.class },
                    this,
                    new Object[] { username }
            );
        }

    }

    @Test
    public void test3() {
        AbsUserManager userManager = new AbsUserManagerImpl();
        userManager.checkUsername("123");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test4() {
        AbsUserManager userManager = new AbsUserManagerImpl();
        userManager.checkUsername(null);
    }

}

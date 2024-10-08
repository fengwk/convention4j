package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fengwk
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class ExceptionResultHandlerTest {

    @Autowired
    private TestService testService;

    @Test
    public void test1() {
        assert testService.test1().getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.getStatus();
    }

    @Test
    public void test2() {
        assertThrows(ArithmeticException.class, () -> {
            testService.test2();
        });
    }

    @Test
    public void test3() {
        testService.checkUser(new TestService.User());
    }

    @Test
    public void testProxy() {
        FooService fooService = new FooService();
        FooService proxy = ResultExceptionHandlerUtils.getProxy(fooService, log);
        Result<Boolean> result = proxy.foo();
        assert result != null;
        assert !result.isSuccess();
    }

    @Test
    public void testProxy2() {
        assertThrows(IllegalArgumentException.class, () -> {
            FooService fooService = new FooService();
            FooService proxy = ResultExceptionHandlerUtils.getProxy(fooService, log);
            proxy.foo2();
        });
    }

    public static class FooService {

        public Result<Boolean> foo() {
            throw new IllegalArgumentException("error");
        }

        public boolean foo2() {
            throw new IllegalArgumentException("error");
        }

    }

}

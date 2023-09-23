package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.HttpStatus;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
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
public class ExceptionResultHandlerTest {

    @Autowired
    private TestService testService;

    @Test
    public void test1() {
        assert testService.test1().getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.getStatus();
    }

    @Test(expected = ArithmeticException.class)
    public void test2() {
        testService.test2();
    }

    @Test
    public void test3() {
        testService.checkUser(new TestService.User());
    }

    @Test
    public void testProxy() {
        FooService fooService = new FooService();
        FooService proxy = ResultExceptionHandlerUtils.getProxy(fooService);
        Result<Boolean> result = proxy.foo();
        assert result != null;
        assert !result.isSuccess();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxy2() {
        FooService fooService = new FooService();
        FooService proxy = ResultExceptionHandlerUtils.getProxy(fooService);
        proxy.foo2();
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

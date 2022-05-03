package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.common.code.CommonCodeTable;
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
        assert CommonCodeTable.ILLEGAL_STATE.equalsCode(testService.test1().getCode());
    }

    @Test(expected = ArithmeticException.class)
    public void test2() {
        testService.test2();
    }

    @Test
    public void test3() {
        testService.checkUser(new TestService.User());
    }

}

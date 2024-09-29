package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ErrorCodeAutoConfigurationTest {

    @Test
    public void test() {
        ConventionErrorCode errorCode1 = TestErrorCodes.TEST.resolve();
        ConventionErrorCode errorCode2 = TestErrorCodes.TEST.resolve("test error");
        assert errorCode1.getCode().equals("TEST_1");
        assert errorCode1.getMessage().equals("测试错误") || errorCode1.getMessage().equals("test error");
        assert errorCode2.getCode().equals("TEST_1");
        assert errorCode2.getMessage().equals("test error");
    }

}

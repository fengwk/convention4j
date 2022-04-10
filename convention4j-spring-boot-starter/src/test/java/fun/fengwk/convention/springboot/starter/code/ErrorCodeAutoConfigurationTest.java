package fun.fengwk.convention.springboot.starter.code;

import fun.fengwk.convention.api.code.ErrorCode;
import fun.fengwk.convention.api.code.ErrorCodeFactory;
import fun.fengwk.convention.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ErrorCodeAutoConfigurationTest {

    private static final String TEST_ERROR_CODE = ErrorCode.encodeCode(ErrorCode.SOURCE_A, "TEST", "0001");
    
    @Autowired
    private ErrorCodeFactory errorCodeFactory;
    
    @Test
    public void test1() {
        ErrorCode errorCode1 = errorCodeFactory.create(TEST_ERROR_CODE);
        ErrorCode errorCode2 = errorCodeFactory.create(TEST_ERROR_CODE, "test error");
        assert errorCode1.getCode().equals(TEST_ERROR_CODE);
        assert errorCode1.getMessage().equals("测试错误") || errorCode1.getMessage().equals("test error");
        assert errorCode2.getCode().equals(TEST_ERROR_CODE);
        assert errorCode2.getMessage().equals("test error");
    }

    @Test
    public void test2() {
        ErrorCode errorCode1 = GlobalErrorCodeFactory.create(TEST_ERROR_CODE);
        ErrorCode errorCode2 = GlobalErrorCodeFactory.create(TEST_ERROR_CODE, "test error");
        assert errorCode1.getCode().equals(TEST_ERROR_CODE);
        assert errorCode1.getMessage().equals("测试错误") || errorCode1.getMessage().equals("test error");
        assert errorCode2.getCode().equals(TEST_ERROR_CODE);
        assert errorCode2.getMessage().equals("test error");
    }
    
}

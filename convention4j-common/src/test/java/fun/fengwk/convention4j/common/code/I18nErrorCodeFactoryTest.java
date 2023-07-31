package fun.fengwk.convention4j.common.code;

import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

/**
 * @author fengwk
 */
public class I18nErrorCodeFactoryTest {

    @Test
    public void test1() throws IOException {
        I18nErrorCodeFactory i18nErrorCodeFactory = new I18nErrorCodeFactory(Locale.SIMPLIFIED_CHINESE,
                I18nErrorCodeFactoryTest.class.getClassLoader());
        ErrorCode errorCode = i18nErrorCodeFactory.create(CommonErrorCodes.ILLEGAL_ARGUMENT);
        assert "参数异常".equals(errorCode.getMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2() throws IOException {
        I18nErrorCodeFactory i18nErrorCodeFactory = new I18nErrorCodeFactory(Locale.SIMPLIFIED_CHINESE,
                I18nErrorCodeFactoryTest.class.getClassLoader());
        i18nErrorCodeFactory.create("-999");
    }

}

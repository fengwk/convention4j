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
        I18nErrorCodeMessageManager i18nErrorCodeMessageManager = new I18nErrorCodeMessageManager(
            Locale.SIMPLIFIED_CHINESE, I18nErrorCodeFactoryTest.class.getClassLoader());
        String message = i18nErrorCodeMessageManager.getMessage(ErrorCodes.BAD_REQUEST);
        assert "错误的请求".equals(message);
    }

}

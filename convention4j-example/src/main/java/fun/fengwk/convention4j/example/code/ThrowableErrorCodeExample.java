package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.code.ErrorCode;
import fun.fengwk.convention4j.common.code.I18nErrorCodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @author fengwk
 */
public class ThrowableErrorCodeExample {

    private static final Logger log = LoggerFactory.getLogger(ThrowableErrorCodeExample.class);

    public static void main(String[] args) {
        I18nErrorCodeFactory errorCodeFactory = new I18nErrorCodeFactory(Locale.getDefault(),
                ErrorCodeExample.class.getClassLoader());
        ErrorCode errorCode = errorCodeFactory.create(ExampleErrorCodes.EXAMPLE_ERROR,
                MapUtils.newMap("name", "fengwk"));

        log.warn("发生了示例错误, 上下文信息是balabala...");
        throw errorCode.asThrowable();
    }

}

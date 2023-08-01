package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.common.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengwk
 */
public class ThrowableErrorCodeExample {

    private static final Logger log = LoggerFactory.getLogger(ThrowableErrorCodeExample.class);

    public static void main(String[] args) {
        ErrorCode errorCode = ExampleErrorCodes.EXAMPLE_ERROR.create(MapUtils.newMap("name", "fengwk"));
        log.warn("发生了示例错误, 上下文信息是balabala...");
        throw errorCode.asThrowable();
    }

}

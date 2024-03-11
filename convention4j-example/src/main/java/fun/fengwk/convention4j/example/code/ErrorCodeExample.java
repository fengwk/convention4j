package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeMessageManagerHolder;
import fun.fengwk.convention4j.common.code.I18nErrorCodeMessageResolver;
import fun.fengwk.convention4j.common.util.MapUtils;

import java.util.Locale;

/**
 * @author fengwk
 */
public class ErrorCodeExample {

    public static void main(String[] args) {
        // 英语
        I18nErrorCodeMessageResolver enErrorCodeMessageResolver = new I18nErrorCodeMessageResolver(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        ErrorCodeMessageManagerHolder.setInstance(enErrorCodeMessageResolver);
        ErrorCode enErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.resolveWithContext(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, hi fengwk, this is example error.>

        // 中文
        I18nErrorCodeMessageResolver cnErrorCodeMessageResolver = new I18nErrorCodeMessageResolver(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        ErrorCodeMessageManagerHolder.setInstance(cnErrorCodeMessageResolver);
        ErrorCode cnErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.resolveWithContext(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, 你好fengwk，这是一个示例错误。>
    }

}

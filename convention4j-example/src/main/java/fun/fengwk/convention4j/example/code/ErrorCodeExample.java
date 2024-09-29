package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.CodeMessageResolverUtils;
import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.common.code.I18nCodeMessageResolver;
import fun.fengwk.convention4j.common.util.MapUtils;

import java.util.Locale;

/**
 * @author fengwk
 */
public class ErrorCodeExample {

    public static void main(String[] args) {
        // 英语
        I18nCodeMessageResolver enErrorCodeMessageResolver = new I18nCodeMessageResolver(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        CodeMessageResolverUtils.setInstance(enErrorCodeMessageResolver);
        ErrorCode enErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.resolve(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, hi fengwk, this is example error.>

        // 中文
        I18nCodeMessageResolver cnErrorCodeMessageResolver = new I18nCodeMessageResolver(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        CodeMessageResolverUtils.setInstance(cnErrorCodeMessageResolver);
        ErrorCode cnErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.resolve(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, 你好fengwk，这是一个示例错误。>
    }

}

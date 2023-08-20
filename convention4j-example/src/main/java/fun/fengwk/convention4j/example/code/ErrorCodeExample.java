package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeMessageManagerHolder;
import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.code.I18nErrorCodeMessageManager;

import java.util.Locale;

/**
 * @author fengwk
 */
public class ErrorCodeExample {

    public static void main(String[] args) {
        // 英语
        I18nErrorCodeMessageManager enErrorCodeMessageManager = new I18nErrorCodeMessageManager(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        ErrorCodeMessageManagerHolder.setInstance(enErrorCodeMessageManager);
        ErrorCode enErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.create(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, hi fengwk, this is example error.>

        // 中文
        I18nErrorCodeMessageManager cnErrorCodeMessageManager = new I18nErrorCodeMessageManager(
            Locale.ENGLISH, ErrorCodeExample.class.getClassLoader());
        ErrorCodeMessageManagerHolder.setInstance(cnErrorCodeMessageManager);
        ErrorCode cnErrorCode = ExampleErrorCodes.EXAMPLE_ERROR.create(MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, 你好fengwk，这是一个示例错误。>
    }

}

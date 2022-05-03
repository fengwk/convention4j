package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.code.ErrorCode;
import fun.fengwk.convention4j.common.code.I18nErrorCodeFactory;

import java.util.Locale;

/**
 * @author fengwk
 */
public class ErrorCodeExample {

    public static void main(String[] args) {
        // 英语
        I18nErrorCodeFactory enErrorCodeFactory = new I18nErrorCodeFactory(Locale.ENGLISH,
                ErrorCodeExample.class.getClassLoader());
        ErrorCode enErrorCode = enErrorCodeFactory.create(ExampleCodeTable.EXAMPLE_ERROR,
                MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, hi fengwk, this is example error.>

        // 中文
        I18nErrorCodeFactory cnErrorCodeFactory = new I18nErrorCodeFactory(Locale.CHINA,
                ErrorCodeExample.class.getClassLoader());
        ErrorCode cnErrorCode = cnErrorCodeFactory.create(ExampleCodeTable.EXAMPLE_ERROR,
                MapUtils.newMap("name", "fengwk"));
        System.out.println(cnErrorCode);
        // 输出：<Example_0001, 你好fengwk，这是一个示例错误。>
    }

}

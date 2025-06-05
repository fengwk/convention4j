package fun.fengwk.convention4j.ai.chat.tool;

import fun.fengwk.convention4j.ai.tool.DefaultChatToolHandlerRegistry;
import fun.fengwk.convention4j.ai.tool.ToolFunctionHandler;
import fun.fengwk.convention4j.ai.tool.ToolFunctionHandlerParser;
import fun.fengwk.convention4j.ai.tool.ToolFunctionHandlerRegistry;
import fun.fengwk.convention4j.ai.tool.annotation.ToolFunction;
import fun.fengwk.convention4j.ai.tool.annotation.ToolFunctionParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author fengwk
 */
public class ToolTest {

    @Test
    public void test() {
        Tools tools = new Tools();

        ToolFunctionHandlerRegistry registry = new DefaultChatToolHandlerRegistry();
        ToolFunctionHandlerParser.parseAndRegister(tools, registry);

        ToolFunctionHandler getWeatherHandler = registry.getHandler("getWeather");

        String date = "2025-06-01";
        String res = getWeatherHandler.call("{\"date\":\"2025-06-01\"}");

        Assertions.assertEquals(getWeather0(date), res);

        ToolFunctionHandler getWeatherHandler2 = registry.getHandler("getWeather2");

        String res2 = getWeatherHandler2.call("{\"date\":\"2025-06-01\"}");

        Assertions.assertEquals(getWeather0(date), res2);
    }

    private static String getWeather0(String date) {
        return date + ": 晴";
    }

    public static class Tools {

        @ToolFunction(description = "查询指定日期的天气")
        public String getWeather(@ToolFunctionParam(name = "date", description = "日期yyyy-MM-dd格式") String date) {
            return getWeather0(date);
        }

        @ToolFunction(description = "查询指定日期的天气")
        public String getWeather2(String date) {
            return getWeather0(date);
        }


    }

}

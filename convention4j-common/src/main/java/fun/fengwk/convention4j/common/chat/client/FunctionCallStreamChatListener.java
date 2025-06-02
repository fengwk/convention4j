package fun.fengwk.convention4j.common.chat.client;

import fun.fengwk.convention4j.common.chat.request.ChatRequest;
import fun.fengwk.convention4j.common.chat.tool.ChatToolHandler;

/**
 * 执行 function call 回调的监听器
 *
 * @author fengwk
 */
public interface FunctionCallStreamChatListener extends StreamChatListener {

    /**
     * 当模型返回响应请求进行 function call 时的回调
     *
     * @param response 模型返回的响应对象，包含 function call 的相关信息
     */
    default void onFunctionCallResponseReceived(ChatCompletionsResponse response) {}

    /**
     * 在即将进行 function call 之前调用的回调
     *
     * @param handler 用于处理 function call 的工具函数处理器
     * @param arguments function call 的参数，通常为 JSON 格式字符串
     */
    default void onPreFunctionCall(ChatToolHandler handler, String arguments) {}

    /**
     * 在 function call 完成后调用的回调
     *
     * @param handler 用于处理 function call 的工具函数处理器
     * @param arguments function call 的参数，通常为 JSON 格式字符串
     * @param result function call 的结果，通常为 JSON 格式字符串
     */
    default void onPostFunctionCall(ChatToolHandler handler, String arguments, String result) {}

    /**
     * 在 function call 调用后，准备发送下一个请求时的回调
     *
     * @param chatRequest 下一个要发送的聊天请求
     */
    default void onNextRequestAfterFunctionCall(ChatRequest chatRequest) {}

}

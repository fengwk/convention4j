package fun.fengwk.convention4j.common.chat.client;

import fun.fengwk.convention4j.common.chat.response.ChatResponse;

/**
 * @author fengwk
 */
public interface StreamChatListener {

    /**
     * 每次接收到响应时被调用
     *
     * @param chatResponse 响应
     */
    default void onReceive(ChatResponse chatResponse) {}

    /**
     * 当所有响应接收完毕时被调用
     */
    default void onComplete(ChatCompletionsResponse response) {}

    /**
     * 当发生错误时被调用
     *
     * @param throwable 异常
     */
    default void onError(Throwable throwable) {}

}

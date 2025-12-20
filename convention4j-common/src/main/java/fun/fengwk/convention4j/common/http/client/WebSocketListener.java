package fun.fengwk.convention4j.common.http.client;

import java.nio.ByteBuffer;

/**
 * WebSocket 事件监听器
 *
 * @author fengwk
 */
public interface WebSocketListener {

    /**
     * 连接建立时调用
     *
     * @param connection WebSocket连接
     */
    default void onOpen(WebSocketConnection connection) {}

    /**
     * 收到文本消息时调用
     *
     * @param message 文本消息
     */
    default void onMessage(String message) {}

    /**
     * 收到二进制消息时调用
     *
     * @param data 二进制数据
     */
    default void onBinary(ByteBuffer data) {}

    /**
     * 连接关闭时调用
     *
     * @param statusCode 状态码
     * @param reason 原因
     */
    default void onClose(int statusCode, String reason) {}

    /**
     * 发生错误时调用
     *
     * @param error 异常
     */
    default void onError(Throwable error) {}

}
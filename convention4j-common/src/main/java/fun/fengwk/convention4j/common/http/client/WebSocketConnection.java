package fun.fengwk.convention4j.common.http.client;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * WebSocket 连接
 *
 * @author fengwk
 */
public interface WebSocketConnection extends AutoCloseable {

    /**
     * 发送文本消息
     *
     * @param message 消息内容
     * @return 结果Future
     */
    CompletableFuture<Void> sendText(String message);

    /**
     * 发送二进制消息
     *
     * @param data 消息数据
     * @return 结果Future
     */
    CompletableFuture<Void> sendBinary(ByteBuffer data);

    /**
     * 发送关闭帧
     *
     * @param statusCode 状态码
     * @param reason 原因
     * @return 结果Future
     */
    CompletableFuture<Void> sendClose(int statusCode, String reason);

    /**
     * 连接是否开启
     *
     * @return 是否开启
     */
    boolean isOpen();

    /**
     * 关闭连接
     */
    @Override
    void close();

}
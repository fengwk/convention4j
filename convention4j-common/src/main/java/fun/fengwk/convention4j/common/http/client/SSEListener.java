package fun.fengwk.convention4j.common.http.client;

/**
 * Server-Sent Events Listener
 *
 * @see <a href="https://www.ruanyifeng.com/blog/2017/05/server-sent_events.html">Server-Sent Events 教程</a>
 * @author fengwk
 */
public interface SSEListener extends StreamBodyListener<String> {

    /**
     * 接收到每一行数据时回调
     *
     * @param line line
     */
    @Override
    default void onReceive(String line) {}

    /**
     * 当接受到 SSEEvent 时调用
     *
     * @param sseEvent sseEvent
     */
    default void onReceiveEvent(SSEEvent sseEvent) {}

    /**
     * 当接受到 comment 时调用
     *
     * @param comment id
     */
    default void onReceiveComment(String comment) {}

}

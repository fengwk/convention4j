package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;

/**
 * Server-Sent Events Listener
 *
 * @see <a href=
 *      "https://www.ruanyifeng.com/blog/2017/05/server-sent_events.html">Server-Sent
 *      Events 教程</a>
 * @author fengwk
 */
public interface SSEListener {

    /**
     * 当初始化时
     *
     * @param responseInfo 响应信息
     */
    default void onInit(HttpResponse.ResponseInfo responseInfo) {
    }

    /**
     * 接收到每一行数据时回调
     *
     * @param line line
     */
    default void onReceive(String line) {
    }

    /**
     * 当读取 body 完成时被调用
     */
    default void onComplete() {
    }

    /**
     * 当流失读取 body 异常时被调用
     *
     * @param throwable 异常
     */
    default void onError(Throwable throwable) {
    }

    /**
     * 当接受到 SSEEvent 时调用
     *
     * @param sseEvent sseEvent
     */
    default void onReceiveEvent(SSEEvent sseEvent) {
    }

}

package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;

/**
 * Server-Sent Events Listener
 *
 * @see <a href="https://www.ruanyifeng.com/blog/2017/05/server-sent_events.html">Server-Sent Events 教程</a>
 * @author fengwk
 */
public interface SSEListener {

    /**
     * 当初始化时
     *
     * @param responseInfo 响应信息
     */
    default void onInit(HttpResponse.ResponseInfo responseInfo) {}

    /**
     * 当接受到 data 时调用
     *
     * @param data data
     */
    default void onReceiveData(String data) {}

    /**
     * 当接受到 event 时调用
     *
     * @param event event
     */
    default void onReceiveEvent(String event) {}

    /**
     * 当接受到 id 时调用
     *
     * @param id id
     */
    default void onReceiveId(String id) {}

    /**
     * 当接受到 retry 时调用
     *
     * @param retry retry
     */
    default void onReceiveRetry(String retry) {}

    /**
     * 当接受到 comment 时调用
     *
     * @param comment comment
     */
    default void onReceiveComment(String comment) {}

    /**
     * 当接收到 SSE 协议外的内容时调用
     *
     * @param other other
     */
    default void onReceiveOther(String other) {}

    /**
     * 当读取 body 完成时被调用
     */
    default void onComplete() {}

    /**
     * 当流失读取 body 异常时被调用
     *
     * @param throwable 异常
     */
    default void onError(Throwable throwable) {}

}

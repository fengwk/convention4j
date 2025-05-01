package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;

/**
 * 流式 body 监听器
 *
 * @author fengwk
 */
public interface StreamBodyListener<T> {

    /**
     * 当初始化时
     *
     * @param responseInfo 响应信息
     */
    void onInit(HttpResponse.ResponseInfo responseInfo);

    /**
     * 当接收到一个 body chunk 时被调用
     *
     * @param chunk chunk
     */
    void onReceive(T chunk);

    /**
     * 当读取 body 完成时被调用
     */
    void onComplete();

    /**
     * 当流失读取 body 异常时被调用
     *
     * @param throwable 异常
     */
    void onError(Throwable throwable);

}

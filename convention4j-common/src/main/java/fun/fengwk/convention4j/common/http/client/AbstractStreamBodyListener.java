package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse.ResponseInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractStreamBodyListener<T> implements StreamBodyListener<T> {

    private volatile boolean done;

    /**
     * 支持抛出异常中断
     */
    protected abstract void onInit0(ResponseInfo responseInfo) throws Exception;

    /**
     * 支持抛出异常中断
     */
    protected abstract void onReceive0(T chunk) throws Exception;

    /**
     * 支持抛出异常中断
     */
    protected abstract void onComplete0() throws Exception;

    /**
     * 支持抛出异常中断
     */
    protected abstract void onError0(Throwable throwable) throws Exception;

    @Override
    public void onInit(ResponseInfo responseInfo) {
        if (done) {
            return;
        }
        try {
            onInit0(responseInfo);
        } catch (Throwable err) {
            onError(err);
        }
    }

    @Override
    public void onReceive(T chunk) {
        if (done) {
            return;
        }
        try {
            onReceive0(chunk);
        } catch (Throwable err) {
            onError(err);
        }
    }

    @Override
    public void onComplete() {
        if (done) {
            return;
        }
        try {
            onComplete0();
        } catch (Throwable err) {
            onError(err);
        }
        done = true;
    }

    @Override
    public void onError(Throwable throwable) {
        if (done) {
            return;
        }
        try {
            onError0(throwable);
        } catch (Throwable err) {
            log.error("failed to execute onError0", err);
        }
        done = true;
    }

}

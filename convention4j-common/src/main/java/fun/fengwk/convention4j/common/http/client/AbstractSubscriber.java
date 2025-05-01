package fun.fengwk.convention4j.common.http.client;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractSubscriber<T> implements Flow.Subscriber<T> {

    private volatile boolean done;

    protected abstract void onSubscribe0(Subscription subscription);

    protected abstract void onNext0(T item);

    protected abstract void onComplete0();

    protected abstract void onError0(Throwable throwable);

    @Override
    public void onSubscribe(Subscription subscription) {
        if (done) {
            return;
        }
        try {
            onSubscribe0(subscription);
        } catch (Throwable err) {
            onError(err);
        }
    }

    @Override
    public void onNext(T item) {
        if (done) {
            return;
        }
        try {
            onNext0(item);
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

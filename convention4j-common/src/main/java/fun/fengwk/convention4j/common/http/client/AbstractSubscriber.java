package fun.fengwk.convention4j.common.http.client;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author fengwk
 */
@Slf4j
public abstract class AbstractSubscriber<T> implements Flow.Subscriber<T> {

    private final AtomicBoolean subscribe = new AtomicBoolean(false);
    private final AtomicBoolean done = new AtomicBoolean(false);
    private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

    protected abstract void onSubscribe0(Subscription subscription) throws Exception;

    protected abstract void onNext0(T item) throws Exception;

    protected abstract void onComplete0() throws Exception;

    protected abstract void onError0(Throwable throwable) throws Exception;

    @Override
    public void onSubscribe(Subscription subscription) {
        if (done.get() || !subscribe.compareAndSet(false, true)) {
            subscription.cancel();
            return;
        }

        subscriptionRef.set(subscription);
        try {
            onSubscribe0(subscription);
        } catch (Throwable err) {
            subscription.cancel();
            onError(err);
        }
    }

    @Override
    public void onNext(T item) {
        if (!done.get()) {
            try {
                onNext0(item);
            } catch (Throwable err) {
                cancelSubscription();
                onError(err);
            }
        }
    }

    @Override
    public void onComplete() {
        if (done.compareAndSet(false, true)) {
            try {
                onComplete0();
            } catch (Throwable err) {
                executeOnError(err);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (done.compareAndSet(false, true)) {
            executeOnError(throwable);
        }
    }

    private void cancelSubscription() {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.cancel();
        }
    }

    private void executeOnError(Throwable throwable) {
        try {
            onError0(throwable);
        } catch (Throwable err) {
            log.error("failed to execute onError0", err);
        }
    }

}

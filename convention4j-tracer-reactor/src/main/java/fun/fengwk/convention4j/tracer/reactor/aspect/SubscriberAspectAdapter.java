package fun.fengwk.convention4j.tracer.reactor.aspect;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.util.context.Context;

import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class SubscriberAspectAdapter<T, S extends Subscriber<T>> implements Subscriber<T> {

    protected final SubscriberAspect aspect;
    protected final S actual;

    public SubscriberAspectAdapter(SubscriberAspect aspect, S actual) {
        this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        this.actual = Objects.requireNonNull(actual, "actual must not be null");
    }

    protected Context internalCurrentContext() {
        return Context.empty();
    }

    @Override
    public void onSubscribe(Subscription s) {
        Object onSubscribeContext = null;
        try {
            onSubscribeContext = aspect.onSubscribeInit(s, internalCurrentContext());
        } catch (Throwable err) {
            log.error("execute onSubscribeInit error", err);
        }
        try {
            aspect.onSubscribeBefore(s, internalCurrentContext(), onSubscribeContext);
        } catch (Throwable err) {
            log.error("execute onSubscribeBefore error", err);
        }
        try {
            actual.onSubscribe(s);
            try {
                aspect.onSubscribeAfter(s, internalCurrentContext(), onSubscribeContext);
            } catch (Throwable err) {
                log.error("execute onSubscribeAfter error", err);
            }
        } finally {
            try {
                aspect.onSubscribeFinally(s, internalCurrentContext(), onSubscribeContext);
            } catch (Throwable err) {
                log.error("execute onSubscribeFinally error", err);
            }
        }
    }

    @Override
    public void onNext(T t) {
        Object onNextContext = null;
        try {
            onNextContext = aspect.onNextInit(t, internalCurrentContext());
        } catch (Throwable err) {
            log.error("execute onNextInit error", err);
        }
        try {
            aspect.onNextBefore(t, internalCurrentContext(), onNextContext);
        } catch (Throwable err) {
            log.error("execute onNextBefore error", err);
        }
        try {
            actual.onNext(t);
            try {
                aspect.onNextAfter(t, internalCurrentContext(), onNextContext);
            } catch (Throwable err) {
                log.error("execute onNextAfter error", err);
            }
        } finally {
            try {
                aspect.onNextFinally(t, internalCurrentContext(), onNextContext);
            } catch (Throwable err) {
                log.error("execute onNextFinally error", err);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        Object onErrorContext = null;
        try {
            onErrorContext = aspect.onErrorInit(t, internalCurrentContext());
        } catch (Throwable err) {
            log.error("execute onErrorInit error", err);
        }
        try {
            aspect.onErrorBefore(t, internalCurrentContext(), onErrorContext);
        } catch (Throwable err) {
            log.error("execute onErrorBefore error", err);
        }
        try {
            actual.onError(t);
            try {
                aspect.onErrorAfter(t, internalCurrentContext(), onErrorContext);
            } catch (Throwable err) {
                log.error("execute onErrorAfter error", err);
            }
        } finally {
            try {
                aspect.onErrorFinally(t, internalCurrentContext(), onErrorContext);
            } catch (Throwable err) {
                log.error("execute onErrorFinally error", err);
            }
        }
    }

    @Override
    public void onComplete() {
        Object onCompleteContext = null;
        try {
            onCompleteContext = aspect.onCompleteInit(internalCurrentContext());
        } catch (Throwable err) {
            log.error("execute onCompleteInit error", err);
        }
        try {
            aspect.onCompleteBefore(internalCurrentContext(), onCompleteContext);
        } catch (Throwable err) {
            log.error("execute onCompleteBefore error", err);
        }
        try {
            actual.onComplete();
            try {
                aspect.onCompleteAfter(internalCurrentContext(), onCompleteContext);
            } catch (Throwable err) {
                log.error("execute onCompleteAfter error", err);
            }
        } finally {
            try {
                aspect.onCompleteFinally(internalCurrentContext(), onCompleteContext);
            } catch (Throwable err) {
                log.error("execute onCompleteFinally error", err);
            }
        }
    }

}


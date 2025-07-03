package fun.fengwk.convention4j.tracer.reactor.aspect;

import org.reactivestreams.Subscription;
import reactor.util.context.ContextView;

/**
 * @author fengwk
 */
public interface SubscriberAspect {

    /* Publisher Aspect */

    default Object subscribeInit(ContextView ctx) {
        return null;
    }

    default void subscribeBefore(ContextView ctx, Object subscribeContext) {
    }

    default void subscribeAfter(ContextView ctx, Object subscribeContext) {
    }

    default void subscribeFinally(ContextView ctx, Object subscribeContext) {
    }


    /* Subscriber Aspect */

    default Object onSubscribeInit(Subscription s, ContextView ctx) {
        return null;
    }

    default void onSubscribeBefore(Subscription s, ContextView ctx, Object onSubscribeContext) {
    }

    default void onSubscribeAfter(Subscription s, ContextView ctx, Object onSubscribeContext) {
    }

    default void onSubscribeFinally(Subscription s, ContextView ctx, Object onSubscribeContext) {
    }

    default Object onNextInit(Object element, ContextView ctx) {
        return null;
    }

    default void onNextBefore(Object element, ContextView ctx, Object onNextContext) {
    }

    default void onNextAfter(Object element, ContextView ctx, Object onNextContext) {
    }

    default void onNextFinally(Object element, ContextView ctx, Object onNextContext) {
    }

    default Object onErrorInit(Throwable t, ContextView ctx) {
        return null;
    }

    default void onErrorBefore(Throwable t, ContextView ctx, Object onErrorContext) {
    }

    default void onErrorAfter(Throwable t, ContextView ctx, Object onErrorContext) {
    }

    default void onErrorFinally(Throwable t, ContextView ctx, Object onErrorContext) {
    }

    default Object onCompleteInit(ContextView ctx) {
        return null;
    }

    default void onCompleteBefore(ContextView ctx, Object onCompleteContext) {
    }

    default void onCompleteAfter(ContextView ctx, Object onCompleteContext) {
    }

    default void onCompleteFinally(ContextView ctx, Object onCompleteContext) {
    }

}


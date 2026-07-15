package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AbstractSubscriberTest {

    @Test
    void shouldForwardCompletionFailureToErrorHandler() {
        AtomicReference<Throwable> error = new AtomicReference<>();
        RuntimeException expected = new RuntimeException("completion failed");
        AbstractSubscriber<String> subscriber = new AbstractSubscriber<>() {
            @Override
            protected void onSubscribe0(Flow.Subscription subscription) {
            }

            @Override
            protected void onNext0(String item) {
            }

            @Override
            protected void onComplete0() {
                throw expected;
            }

            @Override
            protected void onError0(Throwable throwable) {
                error.set(throwable);
            }
        };

        subscriber.onSubscribe(new TestSubscription());
        subscriber.onComplete();

        // Completion failures must be observable by the subscriber's error handler.
        assertSame(expected, error.get());
    }

    @Test
    void shouldCancelSubscriptionsWhenCallbacksFailOrSubscriptionIsDuplicated() {
        TestSubscription subscribeFailure = new TestSubscription();
        TestSubscription nextFailure = new TestSubscription();
        TestSubscription duplicate = new TestSubscription();
        AtomicInteger errors = new AtomicInteger();
        AbstractSubscriber<String> subscriber = new AbstractSubscriber<>() {
            @Override
            protected void onSubscribe0(Flow.Subscription subscription) {
                if (subscription == subscribeFailure) {
                    throw new IllegalStateException("subscribe failed");
                }
            }

            @Override
            protected void onNext0(String item) {
                throw new IllegalArgumentException("next failed");
            }

            @Override
            protected void onComplete0() {
            }

            @Override
            protected void onError0(Throwable throwable) {
                errors.incrementAndGet();
            }
        };

        subscriber.onSubscribe(subscribeFailure);
        subscriber.onSubscribe(duplicate);

        // A failed onSubscribe callback and every duplicate subscription release their upstreams.
        assertEquals(1, subscribeFailure.cancelCount.get());
        assertEquals(1, duplicate.cancelCount.get());
        assertEquals(1, errors.get());

        AtomicInteger nextErrors = new AtomicInteger();
        AbstractSubscriber<String> nextSubscriber = new AbstractSubscriber<>() {
            @Override
            protected void onSubscribe0(Flow.Subscription subscription) {
            }

            @Override
            protected void onNext0(String item) {
                throw new IllegalArgumentException("next failed");
            }

            @Override
            protected void onComplete0() {
            }

            @Override
            protected void onError0(Throwable throwable) {
                nextErrors.incrementAndGet();
            }
        };
        nextSubscriber.onSubscribe(nextFailure);
        nextSubscriber.onNext("value");

        assertEquals(1, nextFailure.cancelCount.get());
        assertEquals(1, nextErrors.get());
    }

    private static class TestSubscription implements Flow.Subscription {
        private final AtomicInteger cancelCount = new AtomicInteger();

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
            cancelCount.incrementAndGet();
        }
    }
}

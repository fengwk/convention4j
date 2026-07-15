package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class AbstractReactiveHttpSendSpecTest {

    @Test
    void shouldPropagateFailureBeforeResponseHeaders() throws Exception {
        HttpClient client = mock(HttpClient.class);
        CompletableFuture<HttpResponse<Void>> sendFuture = new CompletableFuture<>();
        doAnswer(invocation -> sendFuture).when(client).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        RuntimeException expected = new RuntimeException("connection failed");
        CountDownLatch errorReceived = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        new TestSpec(client).send().subscribe(ignored -> { }, throwable -> {
            error.set(throwable);
            errorReceived.countDown();
        });
        sendFuture.completeExceptionally(expected);

        // A failed handshake must terminate the Mono rather than leaving it pending.
        assertTrue(errorReceived.await(1, TimeUnit.SECONDS));
        assertSameOrCause(expected, error.get());
    }

    @Test
    void shouldCancelFutureAssignedAfterEarlyHeaderCancellationAndRejectSecondBodySubscriber() {
        HttpClient client = mock(HttpClient.class);
        CompletableFuture<HttpResponse<Void>> sendFuture = new CompletableFuture<>();
        AtomicReference<HttpResponse.BodySubscriber<Void>> bodySubscriber = new AtomicReference<>();
        doAnswer(invocation -> {
            HttpResponse.BodyHandler<Void> handler = invocation.getArgument(1);
            bodySubscriber.set(handler.apply(responseInfo()));
            return sendFuture;
        }).when(client).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

        AtomicBoolean receivedBeforeFutureCompletion = new AtomicBoolean();
        AtomicReference<ReactiveHttpSendResult<Flux<ByteBuffer>>> result = new AtomicReference<>();
        new TestSpec(client).send().subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(org.reactivestreams.Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(ReactiveHttpSendResult<Flux<ByteBuffer>> value) {
                result.set(value);
                receivedBeforeFutureCompletion.set(!sendFuture.isDone());
                value.getBody().subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(org.reactivestreams.Subscription subscription) {
                        cancel();
                    }
                });
            }
        });

        // Header delivery is still early, and cancellation races cancel the future after it is assigned.
        assertTrue(receivedBeforeFutureCompletion.get());
        assertTrue(sendFuture.isCancelled());

        AtomicReference<Throwable> secondSubscriberError = new AtomicReference<>();
        result.get().getBody().subscribe(ignored -> { }, secondSubscriberError::set);
        assertInstanceOf(IllegalStateException.class, secondSubscriberError.get());
        assertNotNull(bodySubscriber.get());

        TestSubscription upstream = new TestSubscription();
        bodySubscriber.get().onSubscribe(upstream);
        assertEquals(1, upstream.cancelCount.get());
    }

    @Test
    void shouldCancelResponseWhenMonoMapperFailsBeforeConsumingBody() {
        assertMapperFailureCancelsRequest(true);
    }

    @Test
    void shouldCancelResponseWhenFluxMapperFailsBeforeConsumingBody() {
        assertMapperFailureCancelsRequest(false);
    }

    @Test
    void shouldCancelResponseWhenMonoMapperCompletesWithoutConsumingBody() {
        assertUnconsumedMapperCancelsRequest(true);
    }

    @Test
    void shouldCancelResponseWhenFluxMapperCompletesWithoutConsumingBody() {
        assertUnconsumedMapperCancelsRequest(false);
    }

    private static void assertMapperFailureCancelsRequest(boolean monoMapper) {
        HttpClient client = mock(HttpClient.class);
        CompletableFuture<HttpResponse<Void>> sendFuture = new CompletableFuture<>();
        doAnswer(invocation -> {
            HttpResponse.BodyHandler<Void> handler = invocation.getArgument(1);
            handler.apply(responseInfo());
            return sendFuture;
        }).when(client).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost/test")).GET().build();
        RuntimeException expected = new RuntimeException("mapper failed");
        AtomicReference<Throwable> actual = new AtomicReference<>();

        if (monoMapper) {
            new ReactiveHttpSendMonoSpec<>(client, request, response -> {
                throw expected;
            }).send().subscribe(ignored -> { }, actual::set);
        } else {
            new ReactiveHttpSendFluxSpec<>(client, request, response -> {
                throw expected;
            }).send().subscribe(ignored -> { }, actual::set);
        }

        assertSameOrCause(expected, actual.get());
        assertTrue(sendFuture.isCancelled());
    }

    private static void assertUnconsumedMapperCancelsRequest(boolean monoMapper) {
        HttpClient client = mock(HttpClient.class);
        CompletableFuture<HttpResponse<Void>> sendFuture = new CompletableFuture<>();
        doAnswer(invocation -> {
            HttpResponse.BodyHandler<Void> handler = invocation.getArgument(1);
            handler.apply(responseInfo());
            return sendFuture;
        }).when(client).sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost/test")).GET().build();

        if (monoMapper) {
            String value = new ReactiveHttpSendMonoSpec<>(client, request, response -> Mono.just("mapped"))
                .send()
                .block(Duration.ofSeconds(1));
            assertEquals("mapped", value);
        } else {
            List<String> values = new ReactiveHttpSendFluxSpec<>(client, request, response -> Flux.just("mapped"))
                .send()
                .collectList()
                .block(Duration.ofSeconds(1));
            assertEquals(List.of("mapped"), values);
        }

        assertTrue(sendFuture.isCancelled());
    }

    private static HttpResponse.ResponseInfo responseInfo() {
        HttpHeaders headers = HttpHeaders.of(Map.of(), (name, value) -> true);
        return new HttpResponse.ResponseInfo() {
            @Override
            public int statusCode() {
                return 200;
            }

            @Override
            public HttpHeaders headers() {
                return headers;
            }

            @Override
            public HttpClient.Version version() {
                return HttpClient.Version.HTTP_1_1;
            }
        };
    }

    private static void assertSameOrCause(Throwable expected, Throwable actual) {
        if (actual != expected) {
            assertEquals(expected, actual.getCause());
        }
    }

    private static class TestSpec extends AbstractReactiveHttpSendSpec<Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>>> {
        private TestSpec(HttpClient httpClient) {
            super(httpClient, HttpRequest.newBuilder(URI.create("http://localhost/test")).GET().build());
        }

        @Override
        public Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>> send() {
            return send0();
        }
    }

    private static class TestSubscription implements java.util.concurrent.Flow.Subscription {
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

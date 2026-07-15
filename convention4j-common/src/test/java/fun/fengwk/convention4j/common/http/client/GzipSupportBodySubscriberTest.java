package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GzipSupportBodySubscriberTest {

    @Test
    void shouldCompleteBodyFutureExceptionallyForIncompleteGzipEvenWhenDownstreamThrows() throws Exception {
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(gzipResponseInfo(), throwingSubscriber());
        byte[] compressed = gzip("body");
        subscriber.onSubscribe(new TestSubscription());
        subscriber.onNext(List.of(ByteBuffer.wrap(compressed, 0, compressed.length - 2)));
        subscriber.onComplete();

        // A truncated gzip stream must not leave the HTTP client's body future pending.
        ExecutionException exception = assertThrows(ExecutionException.class,
            () -> subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS));
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void shouldCompleteBodyFutureExceptionallyForMalformedGzipEvenWhenDownstreamThrows() {
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(gzipResponseInfo(), throwingSubscriber());
        subscriber.onSubscribe(new TestSubscription());
        subscriber.onNext(List.of(ByteBuffer.wrap("not-gzip".getBytes(StandardCharsets.UTF_8))));

        // Decoder failures and throwing downstream error handlers still settle the body future.
        assertThrows(ExecutionException.class,
            () -> subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS));
    }

    @Test
    void shouldDecodeCompleteGzipAndCompleteBodyFuture() throws Exception {
        List<ByteBuffer> delivered = new ArrayList<>();
        AtomicInteger completions = new AtomicInteger();
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(
            gzipResponseInfo(List.of("identity", "X-GZip")), new Flow.Subscriber<>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(List<ByteBuffer> item) {
                    delivered.addAll(item);
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new AssertionError("complete gzip must not fail", throwable);
                }

                @Override
                public void onComplete() {
                    completions.incrementAndGet();
                }
            });
        subscriber.onSubscribe(new TestSubscription());
        subscriber.onNext(List.of(ByteBuffer.wrap(gzip("body"))));
        subscriber.onComplete();

        subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals("body", new String(BufferUtils.toBytes(delivered), StandardCharsets.UTF_8));
        assertEquals(1, completions.get());
    }

    @Test
    void shouldDecodeConcatenatedGzipMembersFromOneChunk() throws Exception {
        List<ByteBuffer> delivered = new ArrayList<>();
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(
            gzipResponseInfo(), new Flow.Subscriber<>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(List<ByteBuffer> item) {
                    delivered.addAll(item);
                }

                @Override
                public void onError(Throwable throwable) {
                    throw new AssertionError("concatenated gzip must not fail", throwable);
                }

                @Override
                public void onComplete() {
                }
            });
        subscriber.onSubscribe(new TestSubscription());
        subscriber.onNext(List.of(ByteBuffer.wrap(concat(gzip("first"), gzip("second")))));
        subscriber.onComplete();

        subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals("firstsecond", new String(BufferUtils.toBytes(delivered), StandardCharsets.UTF_8));
    }

    @Test
    void shouldRejectGzipWithCorruptedChecksum() throws Exception {
        byte[] compressed = gzip("body");
        compressed[compressed.length - 8] ^= 0x01;
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(
            gzipResponseInfo(), noopSubscriber());
        subscriber.onSubscribe(new TestSubscription());

        subscriber.onNext(List.of(ByteBuffer.wrap(compressed)));

        ExecutionException failure = assertThrows(ExecutionException.class,
            () -> subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS));
        assertTrue(failure.getCause().getMessage().contains("Invalid gzip checksum"));
    }

    @Test
    void shouldDecodeGzipWithEmptyExtraField() throws Exception {
        List<ByteBuffer> delivered = new ArrayList<>();
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(
            gzipResponseInfo(), collectingSubscriber(delivered));
        subscriber.onSubscribe(new TestSubscription());

        subscriber.onNext(List.of(ByteBuffer.wrap(withEmptyExtraField(gzip("body")))));
        subscriber.onComplete();

        subscriber.getBody().toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals("body", new String(BufferUtils.toBytes(delivered), StandardCharsets.UTF_8));
    }

    @Test
    void shouldCancelUpstreamAndBodyFutureWithoutDeliveringLateSignals() throws Exception {
        AtomicReference<Flow.Subscription> downstreamSubscription = new AtomicReference<>();
        AtomicInteger nextSignals = new AtomicInteger();
        AtomicInteger terminalSignals = new AtomicInteger();
        GzipSupportBodySubscriber subscriber = new GzipSupportBodySubscriber(
            gzipResponseInfo(), new Flow.Subscriber<>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    downstreamSubscription.set(subscription);
                }

                @Override
                public void onNext(List<ByteBuffer> item) {
                    nextSignals.incrementAndGet();
                }

                @Override
                public void onError(Throwable throwable) {
                    terminalSignals.incrementAndGet();
                }

                @Override
                public void onComplete() {
                    terminalSignals.incrementAndGet();
                }
            });
        TestSubscription upstream = new TestSubscription();
        subscriber.onSubscribe(upstream);

        downstreamSubscription.get().cancel();

        assertEquals(1, upstream.cancelCount.get());
        assertTrue(subscriber.getBody().toCompletableFuture().isCancelled());
        subscriber.onNext(List.of(ByteBuffer.wrap(gzip("late"))));
        subscriber.onComplete();
        assertEquals(0, nextSignals.get());
        assertEquals(0, terminalSignals.get());
    }

    private static HttpResponse.ResponseInfo gzipResponseInfo() {
        return gzipResponseInfo(List.of("GZip"));
    }

    private static HttpResponse.ResponseInfo gzipResponseInfo(List<String> contentEncodings) {
        HttpHeaders headers = HttpHeaders.of(
            Map.of("Content-Encoding", contentEncodings), (name, value) -> true);
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

    private static Flow.Subscriber<List<ByteBuffer>> throwingSubscriber() {
        return new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
            }

            @Override
            public void onError(Throwable throwable) {
                throw new IllegalStateException("downstream error handler failed");
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private static Flow.Subscriber<List<ByteBuffer>> noopSubscriber() {
        return new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private static Flow.Subscriber<List<ByteBuffer>> collectingSubscriber(List<ByteBuffer> delivered) {
        return new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                delivered.addAll(item);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new AssertionError("gzip must not fail", throwable);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private static byte[] gzip(String body) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
            gzipOut.write(body.getBytes(StandardCharsets.UTF_8));
        }
        return out.toByteArray();
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static byte[] withEmptyExtraField(byte[] gzip) {
        int headerLength = 10;
        byte[] result = new byte[gzip.length + 2];
        System.arraycopy(gzip, 0, result, 0, headerLength);
        result[3] |= 0x04;
        result[headerLength] = 0;
        result[headerLength + 1] = 0;
        System.arraycopy(gzip, headerLength, result, headerLength + 2, gzip.length - headerLength);
        return result;
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

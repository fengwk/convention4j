package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.compress.NonblockingGzipDecoder;
import fun.fengwk.convention4j.common.http.HttpUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_ENCODING;

/**
 * @author fengwk
 */
public class GzipSupportBodySubscriber
        extends AbstractSubscriber<List<ByteBuffer>>
        implements HttpResponse.BodySubscriber<Void> {

    private final Flow.Subscriber<? super List<ByteBuffer>> userSubscriber;
    private final NonblockingGzipDecoder gzipDecoder;
    private final CompletableFuture<Void> resultFuture = new CompletableFuture<>();
    private final BodyCollector bodyCollector;
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private boolean gzipDecoderDestroyed;

    public GzipSupportBodySubscriber(HttpResponse.ResponseInfo responseInfo,
                                     Flow.Subscriber<? super List<ByteBuffer>> userSubscriber) {
        this(responseInfo, userSubscriber, null);
    }

    public GzipSupportBodySubscriber(HttpResponse.ResponseInfo responseInfo,
            Flow.Subscriber<? super List<ByteBuffer>> userSubscriber, BodyCollector bodyCollector) {
        this.userSubscriber = Objects.requireNonNull(userSubscriber);
        this.bodyCollector = bodyCollector;
        boolean gzip = responseInfo.headers().allValues(CONTENT_ENCODING).stream().anyMatch(HttpUtils::gzip);
        if (gzip) {
            this.gzipDecoder = new NonblockingGzipDecoder();
        } else {
            this.gzipDecoder = null;
        }
    }

    @Override
    public CompletionStage<Void> getBody() {
        return resultFuture;
    }

    @Override
    protected void onSubscribe0(Subscription subscription) {
        userSubscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                subscription.request(n);
            }

            @Override
            public void cancel() {
                if (cancelled.compareAndSet(false, true)) {
                    try {
                        subscription.cancel();
                    } finally {
                        cancelBody();
                    }
                }
            }
        });
    }

    @Override
    protected void onNext0(List<ByteBuffer> byteBufferList) {
        if (cancelled.get()) {
            return;
        }
        List<ByteBuffer> processedList;
        if (gzipDecoder == null) {
            processedList = byteBufferList;
        } else {
            processedList = new ArrayList<>();
            synchronized (gzipDecoder) {
                if (cancelled.get()) {
                    return;
                }
                for (ByteBuffer byteBuffer : byteBufferList) {
                    ByteBuffer decodedByteBuffer = gzipDecoder.decode(byteBuffer);
                    processedList.add(decodedByteBuffer);
                }
            }
        }

        if (cancelled.get()) {
            return;
        }
        if (bodyCollector != null) {
            bodyCollector.collect(processedList);
        }

        if (cancelled.get()) {
            return;
        }
        userSubscriber.onNext(processedList);
    }

    @Override
    protected void onComplete0() throws IOException {
        if (cancelled.get()) {
            return;
        }
        if (gzipDecoder != null) {
            synchronized (gzipDecoder) {
                if (cancelled.get()) {
                    return;
                }
                if (!gzipDecoder.isFinished()) {
                    throw new IOException("gzip data is incomplete");
                }
                destroyGzipDecoder();
            }
        }

        if (cancelled.get()) {
            return;
        }
        userSubscriber.onComplete();
        resultFuture.complete(null);
    }

    @Override
    protected void onError0(Throwable throwable) {
        if (cancelled.get()) {
            return;
        }
        try {
            if (gzipDecoder != null) {
                synchronized (gzipDecoder) {
                    destroyGzipDecoder();
                }
            }
        } finally {
            resultFuture.completeExceptionally(throwable);
        }

        if (!cancelled.get()) {
            userSubscriber.onError(throwable);
        }
    }

    private void destroyGzipDecoder() {
        if (!gzipDecoderDestroyed) {
            gzipDecoder.destroy();
            gzipDecoderDestroyed = true;
        }
    }

    private void cancelBody() {
        if (gzipDecoder != null) {
            synchronized (gzipDecoder) {
                destroyGzipDecoder();
            }
        }
        resultFuture.cancel(false);
    }

}

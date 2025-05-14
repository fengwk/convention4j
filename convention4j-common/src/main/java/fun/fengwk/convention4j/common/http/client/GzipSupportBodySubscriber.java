package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.compress.NonblockingGzipDecoder;
import fun.fengwk.convention4j.common.http.HttpUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

import static fun.fengwk.convention4j.common.http.HttpHeaders.CONTENT_ENCODING;

/**
 * @author fengwk
 */
public class GzipSupportBodySubscriber
        extends AbstractSubscriber<List<ByteBuffer>>
        implements HttpResponse.BodySubscriber<List<ByteBuffer>> {

    private final Flow.Subscriber<? super List<ByteBuffer>> userSubscriber;
    private final NonblockingGzipDecoder gzipDecoder;
    private final CompletableFuture<List<ByteBuffer>> resultFuture = new CompletableFuture<>();
    private final List<ByteBuffer> result = Collections.synchronizedList(new ArrayList<>());

    public GzipSupportBodySubscriber(HttpResponse.ResponseInfo responseInfo,
            Flow.Subscriber<? super List<ByteBuffer>> userSubscriber) {
        this.userSubscriber = Objects.requireNonNull(userSubscriber);
        String contentEncoding = responseInfo.headers().firstValue(CONTENT_ENCODING).orElse(null);
        if (HttpUtils.gzip(contentEncoding)) {
            this.gzipDecoder = new NonblockingGzipDecoder();
        } else {
            this.gzipDecoder = null;
        }
    }

    @Override
    public CompletionStage<List<ByteBuffer>> getBody() {
        return resultFuture;
    }

    @Override
    protected void onSubscribe0(Subscription subscription) {
        userSubscriber.onSubscribe(subscription);
    }

    @Override
    protected void onNext0(List<ByteBuffer> byteBufferList) {
        List<ByteBuffer> processedList;
        if (gzipDecoder == null) {
            processedList = byteBufferList;
        } else {
            processedList = new ArrayList<>();
            synchronized (gzipDecoder) {
                for (ByteBuffer byteBuffer : byteBufferList) {
                    ByteBuffer decodedByteBuffer = gzipDecoder.decode(byteBuffer);
                    processedList.add(decodedByteBuffer);
                }
            }
        }

        for (ByteBuffer bb : processedList) {
            result.add(copy(bb));
        }

        userSubscriber.onNext(processedList);
    }

    @Override
    protected void onComplete0() throws IOException {
        if (gzipDecoder != null) {
            synchronized (gzipDecoder) {
                if (!gzipDecoder.isFinished()) {
                    throw new IOException("gzip data is incomplete");
                }
                gzipDecoder.destroy();
            }
        }

        userSubscriber.onComplete();

        resultFuture.complete(result);
    }

    @Override
    protected void onError0(Throwable throwable) {
        if (gzipDecoder != null) {
            synchronized (gzipDecoder) {
                gzipDecoder.destroy();
            }
        }

        userSubscriber.onError(throwable);

        resultFuture.completeExceptionally(throwable);
    }

    private ByteBuffer copy(ByteBuffer bb) {
        ByteBuffer source = bb.duplicate();
        ByteBuffer copy = ByteBuffer.allocate(source.remaining());
        copy.put(source);
        copy.flip();
        return copy;
    }

}

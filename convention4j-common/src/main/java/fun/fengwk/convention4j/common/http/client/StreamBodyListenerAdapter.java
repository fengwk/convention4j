package fun.fengwk.convention4j.common.http.client;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

/**
 * @author fengwk
 */
class StreamBodyListenerAdapter extends AbstractSubscriber<List<ByteBuffer>> {

    private final HttpResponse.ResponseInfo responseInfo;
    private final StreamBodyListener<List<ByteBuffer>> listener;
    private volatile Flow.Subscription subscription;

    public StreamBodyListenerAdapter(HttpResponse.ResponseInfo responseInfo,
                                     StreamBodyListener<List<ByteBuffer>> listener) {
        this.responseInfo = Objects.requireNonNull(responseInfo);
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    protected void onSubscribe0(Subscription subscription) {
        this.subscription = subscription;
        listener.onInit(responseInfo);
        request();
    }

    @Override
    protected void onNext0(List<ByteBuffer> items) {
        listener.onReceive(items);
        request();
    }

    @Override
    protected void onComplete0() {
        listener.onComplete();
    }

    @Override
    protected void onError0(Throwable throwable) {
        listener.onError(throwable);
    }

    private void request() {
        subscription.request(1);
    }

}

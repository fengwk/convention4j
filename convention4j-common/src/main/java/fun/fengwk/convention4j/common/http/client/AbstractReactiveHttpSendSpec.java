package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author fengwk
 */
public abstract class AbstractReactiveHttpSendSpec<T> {

    protected final HttpClient httpClient;
    protected final HttpRequest httpRequest;

    AbstractReactiveHttpSendSpec(HttpClient httpClient, HttpRequest httpRequest) {
        Objects.requireNonNull(httpClient, "httpClient must not be null");
        Objects.requireNonNull(httpRequest, "httpRequest must not be null");
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
    }

    /**
     * 发送 http 请求
     */
    public abstract T send();

    /**
     * 异步发送http请求，注意使用当前方法后，必须主动消费 body，否则将导致底层 TCP 变为僵尸状态
     */
    protected Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>> send0() {
        return Mono.create(emitter -> {
            AtomicReference<CompletableFuture<HttpResponse<Void>>> sendAsyncFutureRef = new AtomicReference<>();
            CompletableFuture<HttpResponse<Void>> sendAsyncFuture = httpClient.sendAsync(httpRequest,
                responseInfo -> {
                    ReactiveHttpSendResult<Flux<ByteBuffer>> result = new ReactiveHttpSendResult<>();
                    result.setStatusCode(responseInfo.statusCode());
                    HttpHeaders headers = responseInfo.headers();
                    if (headers != null) {
                        result.setHeaders(Collections.unmodifiableMap(headers.map()));
                    } else {
                        result.setHeaders(Collections.emptyMap());
                    }

                    LinkedList<Consumer<FluxSink<ByteBuffer>>> signalQueue = new LinkedList<>();
                    AtomicReference<FluxSink<ByteBuffer>> bodyEmitterRef = new AtomicReference<>();
                    Flux<ByteBuffer> body = Flux.<ByteBuffer>create(sink -> {
                            synchronized (bodyEmitterRef) {
                                bodyEmitterRef.set(sink);
                                while (!signalQueue.isEmpty()) {
                                    signalQueue.poll().accept(sink);
                                }
                            }
                        })
                        .doOnCancel(() -> {
                            CompletableFuture<HttpResponse<Void>> future = sendAsyncFutureRef.get();
                            if (future != null) {
                                future.cancel(true);
                            }
                        });
                    result.setBody(body);

                    HttpResponse.BodySubscriber<Void> subscriber = new GzipSupportBodySubscriber(responseInfo,
                        new AbstractSubscriber<>() {
                            @Override
                            protected void onSubscribe0(Flow.Subscription subscription) {
                                synchronized (bodyEmitterRef) {
                                    FluxSink<ByteBuffer> bodyEmitter = bodyEmitterRef.get();
                                    if (bodyEmitter != null) {
                                        bodyEmitter.onRequest(subscription::request);
                                    } else {
                                        signalQueue.offer(sink -> sink.onRequest(subscription::request));
                                    }
                                }
                            }

                            @Override
                            protected void onNext0(List<ByteBuffer> chunks) {
                                FluxSink<ByteBuffer> bodyEmitter = bodyEmitterRef.get();
                                for (ByteBuffer chunk : chunks) {
                                    bodyEmitter.next(chunk);
                                }
                            }

                            @Override
                            protected void onComplete0() {
                                synchronized (bodyEmitterRef) {
                                    FluxSink<ByteBuffer> bodyEmitter = bodyEmitterRef.get();
                                    if (bodyEmitter != null) {
                                        if (!bodyEmitter.isCancelled()) {
                                            bodyEmitter.complete();
                                        }
                                    } else {
                                        signalQueue.offer(sink -> {
                                            if (!sink.isCancelled()) {
                                                sink.complete();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            protected void onError0(Throwable throwable) {
                                synchronized (bodyEmitterRef) {
                                    FluxSink<ByteBuffer> bodyEmitter = bodyEmitterRef.get();
                                    if (bodyEmitter != null) {
                                        bodyEmitter.error(throwable);
                                    } else {
                                        signalQueue.offer(sink -> sink.error(throwable));
                                    }
                                }
                            }
                        });

                    emitter.success(result);

                    return subscriber;
                });

            sendAsyncFutureRef.set(sendAsyncFuture);
            emitter.onCancel(() -> {
                CompletableFuture<HttpResponse<Void>> future = sendAsyncFutureRef.get();
                if (future != null) {
                    future.cancel(true);
                }
            });
        });
    }

}

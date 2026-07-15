package fun.fengwk.convention4j.common.http.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
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
     * 异步发送http请求。响应 body 只允许一个订阅者，且必须被消费或取消；取消响应头 Mono
     * 或 body Flux 会向底层 HTTP future 和 body subscription 传播取消。
     */
    protected Mono<ReactiveHttpSendResult<Flux<ByteBuffer>>> send0() {
        return Mono.create(emitter -> {
            AtomicReference<CompletableFuture<HttpResponse<Void>>> sendAsyncFutureRef = new AtomicReference<>();
            AtomicReference<Flow.Subscription> bodySubscriptionRef = new AtomicReference<>();
            AtomicBoolean cancellationRequested = new AtomicBoolean();
            AtomicBoolean headersDelivered = new AtomicBoolean();
            Runnable cancelRequest = () -> {
                if (!cancellationRequested.compareAndSet(false, true)) {
                    return;
                }
                Flow.Subscription subscription = bodySubscriptionRef.get();
                if (subscription != null) {
                    subscription.cancel();
                }
                CompletableFuture<HttpResponse<Void>> future = sendAsyncFutureRef.get();
                if (future != null) {
                    future.cancel(true);
                }
            };
            emitter.onCancel(cancelRequest::run);

            if (cancellationRequested.get()) {
                return;
            }

            try {
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
                        AtomicBoolean bodySubscribed = new AtomicBoolean();
                        AtomicBoolean bodyTerminated = new AtomicBoolean();
                        Flux<ByteBuffer> body = Flux.<ByteBuffer>defer(() -> {
                                if (!bodySubscribed.compareAndSet(false, true)) {
                                    return Flux.error(new IllegalStateException("response body supports only one subscriber"));
                                }
                                return Flux.create(sink -> {
                                    List<Consumer<FluxSink<ByteBuffer>>> pendingSignals;
                                    synchronized (bodyEmitterRef) {
                                        bodyEmitterRef.set(sink);
                                        pendingSignals = new ArrayList<>(signalQueue);
                                        signalQueue.clear();
                                    }
                                    for (Consumer<FluxSink<ByteBuffer>> signal : pendingSignals) {
                                        signal.accept(sink);
                                    }
                                });
                            })
                            .doOnCancel(cancelRequest);
                        result.setBody(body);
                        result.setBodyCleanup(() -> {
                            if (!bodyTerminated.get()) {
                                cancelRequest.run();
                            }
                        });

                        HttpResponse.BodySubscriber<Void> subscriber = new GzipSupportBodySubscriber(responseInfo,
                            new AbstractSubscriber<>() {
                                @Override
                                protected void onSubscribe0(Flow.Subscription subscription) {
                                    bodySubscriptionRef.set(subscription);
                                    if (cancellationRequested.get()) {
                                        subscription.cancel();
                                        return;
                                    }
                                    Consumer<FluxSink<ByteBuffer>> signal =
                                        sink -> sink.onRequest(subscription::request);
                                    FluxSink<ByteBuffer> bodyEmitter;
                                    synchronized (bodyEmitterRef) {
                                        bodyEmitter = bodyEmitterRef.get();
                                        if (bodyEmitter == null) {
                                            signalQueue.offer(signal);
                                        }
                                    }
                                    if (bodyEmitter != null) {
                                        signal.accept(bodyEmitter);
                                    }
                                }

                                @Override
                                protected void onNext0(List<ByteBuffer> chunks) {
                                    Consumer<FluxSink<ByteBuffer>> signal = sink -> {
                                        for (ByteBuffer chunk : chunks) {
                                            sink.next(chunk);
                                        }
                                    };
                                    FluxSink<ByteBuffer> bodyEmitter;
                                    synchronized (bodyEmitterRef) {
                                        bodyEmitter = bodyEmitterRef.get();
                                        if (bodyEmitter == null) {
                                            signalQueue.offer(signal);
                                        }
                                    }
                                    if (bodyEmitter != null) {
                                        signal.accept(bodyEmitter);
                                    }
                                }

                                @Override
                                protected void onComplete0() {
                                    bodyTerminated.set(true);
                                    Consumer<FluxSink<ByteBuffer>> signal = sink -> {
                                        if (!sink.isCancelled()) {
                                            sink.complete();
                                        }
                                    };
                                    FluxSink<ByteBuffer> bodyEmitter;
                                    synchronized (bodyEmitterRef) {
                                        bodyEmitter = bodyEmitterRef.get();
                                        if (bodyEmitter == null) {
                                            signalQueue.offer(signal);
                                        }
                                    }
                                    if (bodyEmitter != null) {
                                        signal.accept(bodyEmitter);
                                    }
                                }

                                @Override
                                protected void onError0(Throwable throwable) {
                                    bodyTerminated.set(true);
                                    Consumer<FluxSink<ByteBuffer>> signal = sink -> sink.error(throwable);
                                    FluxSink<ByteBuffer> bodyEmitter;
                                    synchronized (bodyEmitterRef) {
                                        bodyEmitter = bodyEmitterRef.get();
                                        if (bodyEmitter == null) {
                                            signalQueue.offer(signal);
                                        }
                                    }
                                    if (bodyEmitter != null) {
                                        signal.accept(bodyEmitter);
                                    }
                                }
                            });

                        headersDelivered.set(true);
                        emitter.success(result);
                        return subscriber;
                    });

                sendAsyncFutureRef.set(sendAsyncFuture);
                if (cancellationRequested.get()) {
                    sendAsyncFuture.cancel(true);
                }
                sendAsyncFuture.whenComplete((response, throwable) -> {
                    if (throwable != null && !headersDelivered.get()) {
                        emitter.error(throwable);
                    }
                });
            } catch (Throwable throwable) {
                emitter.error(throwable);
            }
        });
    }

}

package fun.fengwk.convention4j.springboot.starter.web.util;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.result.ResultExceptionHandlerUtils;
import fun.fengwk.convention4j.common.result.Results;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Slf4j
public class SseUtils {

    private static final String DONE = "[DONE]";

    private SseUtils() {
    }

    /**
     * 发送 SSE {@link fun.fengwk.convention4j.api.result.Result} 事件，使用默认的异常处理器处理异常结果，使用{@link #DONE}作为结尾
     *
     * @param flux         事件流
     * @param timeout      SSE超时时间
     * @param response     响应对象
     * @return SseEmitter
     */
    public static <T> SseEmitter sendSSEResult(Flux<T> flux, Duration timeout, HttpServletResponse response) {
        return sendSSEResult(flux, DONE, err -> JsonUtils.toJson(ResultExceptionHandlerUtils.handleError(err, log)), timeout, response);
    }

    /**
     * 发送 SSE {@link fun.fengwk.convention4j.api.result.Result} 事件
     *
     * @param flux         事件流
     * @param done         完成事件
     * @param errorHandler 错误事件处理器
     * @param timeout      SSE超时时间
     * @param response     响应对象
     * @return SseEmitter
     */
    public static <T> SseEmitter sendSSEResult(Flux<T> flux, String done, Function<Throwable, String> errorHandler,
                                     Duration timeout, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);

        SseEmitter sseEmitter = timeout == null ? new SseEmitter() : new SseEmitter(timeout.toMillis());
        SseResourceManager sseResourceManager = new SseResourceManager(sseEmitter);

        Disposable disposable = flux
            .map(Results::ok)
            .map(JsonUtils::toJson)
            .doOnNext(sseResourceManager::sendData)
            .doOnCancel(() -> {
                log.info("SSE stream cancelled by client");
                sseResourceManager.safeComplete();
            })
            .doOnComplete(() -> {
                sseResourceManager.sendData(done);
                sseResourceManager.safeComplete();
            })
            .doOnError(err -> {
                log.error("An error occurs in SSE stream", err);
                String errorData = errorHandler.apply(err);
                if (errorData != null) {
                    sseResourceManager.sendData(errorData);
                }
                sseResourceManager.safeComplete();
            })
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();

        sseResourceManager.addDisposable(disposable);

        // 统一的资源清理handler
        sseEmitter.onCompletion(sseResourceManager::disposeResources);
        sseEmitter.onTimeout(sseResourceManager::disposeResources);
        sseEmitter.onError(err -> {
            log.error("SSE stream error", err);
            sseResourceManager.disposeResources();
        });

        return sseEmitter;
    }

    // 线程安全的资源管理类
    static class SseResourceManager {

        private final SseEmitter sseEmitter;
        private final AtomicBoolean completedFlag = new AtomicBoolean(false);
        private final Disposable.Composite composite = Disposables.composite();

        SseResourceManager(SseEmitter sseEmitter) {
            this.sseEmitter = sseEmitter;
        }

        void sendData(String data) {
            try {
                sseEmitter.send(SseEmitter.event().data(data));
            } catch (IOException ex) {
                // 通常是客户端关闭了连接
                log.warn("SSE send error", ex);
                safeComplete();
            }
        }

        void safeComplete() {
            if (completedFlag.compareAndSet(false, true)) {
                try {
                    sseEmitter.complete();
                } finally {
                    disposeResources();
                }
            }
        }

        void disposeResources() {
            composite.dispose();
        }

        void addDisposable(Disposable disposable) {
            composite.add(disposable);
        }

    }

}

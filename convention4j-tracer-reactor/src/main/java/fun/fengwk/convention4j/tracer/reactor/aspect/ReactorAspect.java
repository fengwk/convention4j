package fun.fengwk.convention4j.tracer.reactor.aspect;

import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.*;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * reactor切面
 *
 * @author fengwk
 */
@Slf4j
public class ReactorAspect {

    /**
     * 注册全局切面
     *
     * @param aspectName 切面名称
     * @param aspect     切面
     */
    public static <T> void registerAspect(String aspectName, SubscriberAspect aspect) {
        Hooks.onEachOperator(aspectName, publisher -> {
            if (publisher instanceof Flux) {
                return new AspectFlux<>((Flux<?>) publisher, aspect);
            } else if (publisher instanceof Mono) {
                return new AspectMono<>((Mono<?>) publisher, aspect);
            } else if (publisher instanceof ParallelFlux) {
                return new AspectParallelFlux<>((ParallelFlux<?>) publisher, aspect);
            } else if (publisher instanceof ConnectableFlux) {
                return new AspectConnectableFlux<>((ConnectableFlux<?>) publisher, aspect);
            } else if (publisher instanceof GroupedFlux) {
                return new AspectGroupedFlux<>((GroupedFlux<?, ?>) publisher, aspect);
            } else {
                log.debug("publisher not support aspect, publisher: {}", publisher.getClass().getSimpleName());
                return publisher;
            }
        });
    }

    private static void executeSubscribeWithAspect(Runnable executor, SubscriberAspect aspect, Context context) {
        Object subscribeContext = null;
        try {
            subscribeContext = aspect.subscribeInit(context);
        } catch (Throwable err) {
            log.error("execute subscribeInit error", err);
        }
        try {
            aspect.subscribeBefore(context, subscribeContext);
        } catch (Throwable err) {
            log.error("execute subscribeBefore error", err);
        }
        try {
            executor.run();
            try {
                aspect.subscribeAfter(context, subscribeContext);
            } catch (Throwable err) {
                log.error("execute subscribeAfter error", err);
            }
        } finally {
            try {
                aspect.subscribeFinally(context, subscribeContext);
            } catch (Throwable err) {
                log.error("execute subscribeFinally error", err);
            }
        }
    }

    static class AspectFlux<T> extends Flux<T> {

        private final Flux<? extends T> source;
        private final SubscriberAspect aspect;

        public AspectFlux(Flux<? extends T> source, SubscriberAspect aspect) {
            this.source = Objects.requireNonNull(source, "source must not be null");
            this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        }

        @Override
        public void subscribe(CoreSubscriber<? super T> actual) {
            CoreSubscriber<? super T> actualAspect = new CoreSubscriberAspectAdapter<>(aspect, actual);
            Context context = actualAspect.currentContext();
            executeSubscribeWithAspect(() -> source.subscribe(actualAspect), aspect, context);
        }

    }

    static class AspectMono<T> extends Mono<T> {

        private final Mono<? extends T> source;
        private final SubscriberAspect aspect;

        public AspectMono(Mono<? extends T> source, SubscriberAspect aspect) {
            this.source = Objects.requireNonNull(source, "source must not be null");
            this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        }

        @Override
        public void subscribe(CoreSubscriber<? super T> actual) {
            CoreSubscriber<? super T> actualAspect = new CoreSubscriberAspectAdapter<>(aspect, actual);
            Context context = actualAspect.currentContext();
            executeSubscribeWithAspect(() -> source.subscribe(actualAspect), aspect, context);
        }

    }

    static class AspectParallelFlux<T> extends ParallelFlux<T> {

        private final ParallelFlux<? extends T> source;
        private final SubscriberAspect aspect;

        public AspectParallelFlux(ParallelFlux<? extends T> source, SubscriberAspect aspect) {
            this.source = Objects.requireNonNull(source, "source must not be null");
            this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        }

        @Override
        public int parallelism() {
            return source.parallelism();
        }

        @Override
        public void subscribe(CoreSubscriber<? super T>[] subscribers) {
            if (subscribers == null || subscribers.length == 0) {
                return;
            }
            CoreSubscriber<? super T>[] actualAspects = new CoreSubscriber[subscribers.length];
            for (int i = 0; i < subscribers.length; i++) {
                actualAspects[i] = new CoreSubscriberAspectAdapter<>(aspect, subscribers[i]);
            }
            Context context = actualAspects[0].currentContext();
            executeSubscribeWithAspect(() -> source.subscribe(actualAspects), aspect, context);
        }

    }

    static class AspectConnectableFlux<T> extends ConnectableFlux<T> {

        private final ConnectableFlux<? extends T> source;
        private final SubscriberAspect aspect;

        public AspectConnectableFlux(ConnectableFlux<? extends T> source, SubscriberAspect aspect) {
            this.source = Objects.requireNonNull(source, "source must not be null");
            this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        }

        @Override
        public void connect(Consumer<? super Disposable> cancelSupport) {
            source.connect(cancelSupport);
        }

        @Override
        public void subscribe(CoreSubscriber<? super T> actual) {
            CoreSubscriber<? super T> actualAspect = new CoreSubscriberAspectAdapter<>(aspect, actual);
            Context context = actualAspect.currentContext();
            executeSubscribeWithAspect(() -> source.subscribe(actualAspect), aspect, context);
        }

    }

    static class AspectGroupedFlux<K, T> extends GroupedFlux<K, T> {

        private final GroupedFlux<K, ? extends T> source;
        private final SubscriberAspect aspect;

        public AspectGroupedFlux(GroupedFlux<K, ? extends T> source, SubscriberAspect aspect) {
            this.source = Objects.requireNonNull(source, "source must not be null");
            this.aspect = Objects.requireNonNull(aspect, "aspect must not be null");
        }

        @Override
        public K key() {
            return source.key();
        }

        @Override
        public void subscribe(CoreSubscriber<? super T> actual) {
            CoreSubscriber<? super T> actualAspect = new CoreSubscriberAspectAdapter<>(aspect, actual);
            Context context = actualAspect.currentContext();
            executeSubscribeWithAspect(() -> source.subscribe(actualAspect), aspect, context);
        }

    }

}

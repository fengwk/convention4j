package fun.fengwk.convention4j.tracer.reactor;

import fun.fengwk.convention4j.tracer.finisher.Slf4jSpanFinisher;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import fun.fengwk.convention4j.tracer.util.SpanPropagation;
import fun.fengwk.convention4j.tracer.util.TracerUtils;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author fengwk
 */
@Slf4j
public class ReactorTracerTest {

    static {
        ReactorTracerUtils.initialize(new Slf4jSpanFinisher());
    }

    private void checkId(AtomicReference<String> idRef, String id) {
        if (idRef.get() == null) {
            idRef.set(id);
        } else {
            Assertions.assertEquals(idRef.get(), id);
        }
    }

    @Test
    public void testEmptyMono() {
        // Mono.empty()
        // .doOnCancel(() -> System.out.println("cancel"))
        // .doOnTerminate(() -> System.out.println("terminal"))
        // .subscribe();

        // Mono.empty()
        // .doOnTerminate(() -> System.out.println("Original doOnTerminate"))
        // .switchIfEmpty(Mono.empty()) // 模拟原始代码中的switchIfEmpty(flux)
        // .doOnTerminate(() -> System.out.println("Final doOnTerminate"))
        // .subscribe();

        // Mono.empty()
        // .flatMap(i -> {
        // System.out.println("flatMap: " + i);
        // throw new IllegalStateException("faltMap: " + i);
        // }).doOnError(ex -> {
        // System.out.println("onErrorResume: " + ex);
        //// return Mono.just(3);
        // })
        // .switchIfEmpty(Mono.defer(() -> {
        // System.out.println("switchIfEmpty");
        // throw new IllegalStateException("switchIfEmpty" );
        // }))
        //
        // .subscribe();

        // Mono.fromRunnable(() -> {
        // throw new IllegalStateException("111");
        // })
        // .doOnError(ex -> System.out.println("errrrrr" + ex))
        // .subscribe();

        // Mono.empty()
        // .thenReturn(1)
        // .doOnNext(System.out::println)
        // .subscribe();

        Mono.just(1)
                .mapNotNull(i -> null)

                .subscribe();
    }

    @Test
    public void testMonoTrace() {
        AtomicReference<String> traceIdRef = new AtomicReference<>();
        AtomicReference<String> spanIdRef = new AtomicReference<>();

        SpanInfo spanInfo = SpanInfo.builder().operationName("testMonoTest")
                .propagation(SpanPropagation.REQUIRED).build();

        Mono<Integer> mono = Mono.just(1)
                .flatMap(s -> {
                    String traceId = MDC.get("traceId");
                    String spanId = MDC.get("spanId");
                    checkId(traceIdRef, traceId);
                    checkId(spanIdRef, spanId);
                    System.out.println("[flatMap] traceId: " + traceId);
                    System.out.println("[flatMap] spanId: " + spanId);

                    Mono<Object> subMono = Mono.fromRunnable(() -> {
                        Span span = GlobalTracer.get().activeSpan();
                        SpanContext spanContext = span.context();
                        String subTraceId = spanContext.toTraceId();
                        String subSpanId = spanContext.toSpanId();
                        System.out.println("[subFlatMap] subTraceId: " + subTraceId);
                        System.out.println("[subFlatMap] subSpanId: " + subSpanId);
                    });

                    SpanInfo subSpanInfo = SpanInfo.builder().operationName("subTestMonoTest")
                            .propagation(SpanPropagation.SUPPORTS).build();
                    subMono = ReactorTracerUtils.newSpan(subMono, subSpanInfo);

                    return subMono.then(Mono.just(s));
                })
                .map(s -> {
                    SpanContext spanContext = GlobalTracer.get().activeSpan().context();
                    String traceId = spanContext.toTraceId();
                    String spanId = spanContext.toSpanId();
                    System.out.println("[map] traceId: " + traceId);
                    System.out.println("[map] spanId: " + spanId);
                    checkId(traceIdRef, traceId);
                    checkId(spanIdRef, spanId);
                    return s;
                });

        mono = ReactorTracerUtils.newSpan(mono, spanInfo);

        mono.subscribe();
    }

    @Test
    public void testFluxTrace() {
        AtomicReference<String> traceIdRef = new AtomicReference<>();
        AtomicReference<String> spanIdRef = new AtomicReference<>();

        SpanInfo spanInfo = SpanInfo.builder().operationName("testMonoTest")
                .propagation(SpanPropagation.REQUIRED).build();

        Flux<Integer> flux = Flux.just(1, 2, 3)
                .flatMap(s -> {
                    System.out.println("Flux.flatMap: " + s);
                    String traceId = MDC.get("traceId");
                    String spanId = MDC.get("spanId");
                    checkId(traceIdRef, traceId);
                    checkId(spanIdRef, spanId);
                    System.out.println("[flatMap] traceId: " + traceId);
                    System.out.println("[flatMap] spanId: " + spanId);

                    Mono<Object> subMono = Mono.fromRunnable(() -> {
                        System.out.println("Mono.fromRunnable: " + s);
                        Span span = GlobalTracer.get().activeSpan();
                        SpanContext spanContext = span.context();
                        String subTraceId = spanContext.toTraceId();
                        String subSpanId = spanContext.toSpanId();
                        System.out.println("[subFlatMap] subTraceId: " + subTraceId);
                        System.out.println("[subFlatMap] subSpanId: " + subSpanId);
                    });

                    SpanInfo subSpanInfo = SpanInfo.builder().operationName("subTestMonoTest")
                            .propagation(SpanPropagation.SUPPORTS).build();
                    subMono = ReactorTracerUtils.newSpan(subMono, subSpanInfo);

                    Flux<Integer> subFlux = Flux.just(1, 2, 3).doOnNext(subI -> {
                        System.out.println("subFlux.doOnNext: " + s + "-" + subI);
                        Span span = GlobalTracer.get().activeSpan();
                        SpanContext spanContext = span.context();
                        String subTraceId = spanContext.toTraceId();
                        String subSpanId = spanContext.toSpanId();
                        System.out.println("[subFlatMap] subTraceId: " + subTraceId);
                        System.out.println("[subFlatMap] subSpanId: " + subSpanId);
                    });

                    SpanInfo subFluxSpanInfo = SpanInfo.builder().operationName("subTestFluxTest")
                            .propagation(SpanPropagation.SUPPORTS).build();
                    subFlux = ReactorTracerUtils.newSpan(subFlux, subFluxSpanInfo);
                    subFlux.subscribe();

                    return subMono.then(Mono.just(s));
                })
                .map(s -> {
                    System.out.println("Flux.map: " + s);
                    SpanContext spanContext = GlobalTracer.get().activeSpan().context();
                    String traceId = spanContext.toTraceId();
                    String spanId = spanContext.toSpanId();
                    System.out.println("[map] traceId: " + traceId);
                    System.out.println("[map] spanId: " + spanId);
                    checkId(traceIdRef, traceId);
                    checkId(spanIdRef, spanId);
                    return s;
                });

        flux = ReactorTracerUtils.newSpan(flux, spanInfo);

        flux.subscribe();
    }

    @Test
    public void testNoFlux() {
        SpanInfo spanInfo = SpanInfo.builder()
                .operationName("test").propagation(SpanPropagation.REQUIRED).build();
        TracerUtils.execute(() -> {
            Span span = GlobalTracer.get().activeSpan();
            Assertions.assertNotNull(span);
            SpanContext context = span.context();
            Assertions.assertNotNull(context);
            System.out.println("traceId: " + context.toTraceId());
            System.out.println("spanId: " + context.toSpanId());
            subSpanTest();
        }, spanInfo);
    }

    @Test
    public void testNoFluxToFlux() {
        SpanInfo spanInfo = SpanInfo.builder()
                .operationName("test").propagation(SpanPropagation.REQUIRED).build();
        TracerUtils.execute(() -> {
            Span span = GlobalTracer.get().activeSpan();
            Assertions.assertNotNull(span);
            SpanContext context = span.context();
            Assertions.assertNotNull(context);
            System.out.println("traceId: " + context.toTraceId());
            System.out.println("spanId: " + context.toSpanId());

            Mono<Integer> mono = Mono.just(1)
                    .doOnNext(i -> {
                        Span subSpan = GlobalTracer.get().activeSpan();
                        Assertions.assertNotNull(subSpan);
                        SpanContext subContext = subSpan.context();
                        Assertions.assertNotNull(subContext);
                        System.out.println("fluxSubTraceId: " + subContext.toTraceId());
                        System.out.println("fluxSubSpanId: " + subContext.toSpanId());
                        subSpanTest();
                    });

            SpanInfo subSpanInfo = SpanInfo.builder()
                    .operationName("subFluxTest").propagation(SpanPropagation.REQUIRED).build();

            mono = ReactorTracerUtils.newSpan(mono, subSpanInfo);
            mono.subscribe();

        }, spanInfo);
    }

    private void subSpanTest() {
        SpanInfo subSpanInfo = SpanInfo.builder()
                .operationName("subSpanTest").propagation(SpanPropagation.REQUIRED).build();

        TracerUtils.execute(() -> {
            Span subSpan = GlobalTracer.get().activeSpan();
            Assertions.assertNotNull(subSpan);
            SpanContext subContext = subSpan.context();
            Assertions.assertNotNull(subContext);
            System.out.println("subTraceId: " + subContext.toTraceId());
            System.out.println("subSpanId: " + subContext.toSpanId());
        }, subSpanInfo);
    }

}

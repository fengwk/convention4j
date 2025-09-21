package fun.fengwk.convention4j.springboot.starter.webflux;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author fengwk
 */
@Slf4j
@RestController
@SpringBootApplication
public class WebFluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxTestApplication.class, args);
    }

    @GetMapping("/hello")
    public Mono<Result<String>> hello() {
        log.info("hello");
        Flux<Integer> flux = Flux.fromArray(new Integer[]{1, 2, 3})
            .doOnNext(i -> {
                log.info("next: {}", i);
            });
        Tracer tracer = GlobalTracer.get();
        SpanInfo spanInfo = SpanInfo.builder()
            .operationName("subHello")
            .build();
        flux = ReactorTracerUtils.newSpan(tracer, spanInfo, flux);

        return flux.collect(() -> new AtomicInteger(0), AtomicInteger::addAndGet)
            .flatMap(count -> {
                log.info("count: {}", count);
                return Mono.just(Results.ok("hello: " + count));
            });
    }

}

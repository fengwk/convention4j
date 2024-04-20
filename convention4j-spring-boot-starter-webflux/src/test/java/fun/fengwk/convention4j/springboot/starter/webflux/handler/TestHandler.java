package fun.fengwk.convention4j.springboot.starter.webflux.handler;

import fun.fengwk.convention4j.springboot.starter.webflux.context.TraceInfo;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


/**
 * @author fengwk
 */
@Slf4j
@Component
public class TestHandler {

    public Mono<ServerResponse> hello1(ServerRequest request) {
        log.info("execute hello1, request: {}", request);
        return WebFluxTracerContext.get().flatMap(tc -> tc.execute(() -> {
            SpanInfo spanInfo = SpanInfo.builder().operationName("hello1-inner").build();
            TraceInfo ti = tc.activate(spanInfo);
            log.info("execute hello1 inner");
            tc.finish(ti);
            return ServerResponse.status(200).contentType(MediaType.TEXT_PLAIN).bodyValue("hello1");
        }));
    }

}

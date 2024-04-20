package fun.fengwk.convention4j.springboot.starter.webflux.handler;

import fun.fengwk.convention4j.springboot.starter.webflux.context.TraceInfo;
import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxTracerContext;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;


/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
@Component
public class TestHandler {

    private final WebClient.Builder webClientBuilder;

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

    public Mono<ServerResponse> hello2(ServerRequest request) {
        URI uri = UriComponentsBuilder.fromUriString("https://www.baidu.com")
            .build().toUri();
        Mono<String> resMono = webClientBuilder.build().get()
            .uri(uri)
            .retrieve()
            .bodyToMono(String.class);
        return resMono.flatMap(res -> {
            return ServerResponse.status(200)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(res);
        });
    }

}

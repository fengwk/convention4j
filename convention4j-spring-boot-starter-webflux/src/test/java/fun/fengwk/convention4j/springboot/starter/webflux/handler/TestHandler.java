package fun.fengwk.convention4j.springboot.starter.webflux.handler;

import fun.fengwk.convention4j.tracer.reactor.ReactorTracerUtils;
import fun.fengwk.convention4j.tracer.util.SpanInfo;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
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
        SpanInfo spanInfo = SpanInfo.builder().operationName("hello1-inner").build();
        Mono<ServerResponse> mono = Mono.fromRunnable(() -> log.info("execute hello1 inner"))
                .then(ServerResponse.status(200).contentType(MediaType.TEXT_PLAIN).bodyValue("hello1"));
        Tracer tracer = GlobalTracer.get();
        ReactorTracerUtils.newSpan(tracer, spanInfo, mono);
        return mono;
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

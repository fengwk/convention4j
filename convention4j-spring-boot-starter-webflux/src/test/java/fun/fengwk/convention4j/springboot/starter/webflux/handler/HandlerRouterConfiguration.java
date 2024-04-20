package fun.fengwk.convention4j.springboot.starter.webflux.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * @author fengwk
 */
@Configuration
public class HandlerRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> handlerRouter(TestHandler testHandler) {
        return RouterFunctions.route(GET("/api/hello1"), testHandler::hello1);
    }

}

package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import fun.fengwk.convention4j.springboot.starter.webflux.context.WebFluxContext;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * WebClient请求修改器
 *
 * @author fengwk
 */
public interface WebClientRequestModifier {

    /**
     * 修改WebClient请求
     *
     * @param context        WebFlux上下文
     * @param requestBuilder WebClient请求构建器
     */
    void modify(WebFluxContext context, ClientRequest.Builder requestBuilder);

}

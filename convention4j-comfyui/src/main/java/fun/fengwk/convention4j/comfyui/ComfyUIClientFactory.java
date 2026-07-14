package fun.fengwk.convention4j.comfyui;

import fun.fengwk.convention4j.comfyui.internal.DefaultComfyUIClient;

import java.net.http.HttpClient;

/**
 * ComfyUI客户端工厂
 * 使用实例方法而非静态方法，符合面向对象设计原则
 *
 * @author fengwk
 */
public class ComfyUIClientFactory {

    private final HttpClient httpClient;

    /**
     * 创建默认工厂；客户端会按 ComfyUIClientOptions 构建 HttpClient。
     */
    public ComfyUIClientFactory() {
        this.httpClient = null;
    }

    /**
     * 使用自定义HttpClient创建工厂
     */
    public ComfyUIClientFactory(HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("httpClient must not be null");
        }
        this.httpClient = httpClient;
    }

    /**
     * 创建客户端
     */
    public ComfyUIClient create(ComfyUIClientOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        if (options.getHttpClient() == null && httpClient != null) {
            // 如果options中没有指定httpClient，则使用工厂的
            options = ComfyUIClientOptions.builder()
                     .baseUrl(options.getBaseUrl())
                     .apiKey(options.getApiKey())
                     .connectTimeout(options.getConnectTimeout())
                     .readTimeout(options.getReadTimeout())
                     .websocketTimeout(options.getWebsocketTimeout())
                     .httpClient(httpClient)
                     .build();
        }
        return new DefaultComfyUIClient(options);
    }

    /**
     * 使用默认配置创建客户端
     */
    public ComfyUIClient create(String baseUrl) {
        return create(ComfyUIClientOptions.builder()
                .baseUrl(baseUrl)
                .httpClient(httpClient)
                .build());
    }

    /**
     * 使用默认配置创建客户端，带API Key
     */
    public ComfyUIClient create(String baseUrl, String apiKey) {
        return create(ComfyUIClientOptions.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .httpClient(httpClient)
                .build());
    }
}

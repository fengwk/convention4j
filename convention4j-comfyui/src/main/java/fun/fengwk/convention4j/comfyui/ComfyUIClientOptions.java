package fun.fengwk.convention4j.comfyui;

import lombok.Builder;
import lombok.Getter;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 客户端配置选项
 *
 * @author fengwk
 */
@Builder
@Getter
public class ComfyUIClientOptions {
    /**
     * ComfyUI服务器地址，例如 http://localhost:8188
     */
    private final String baseUrl;

    /**
     * API密钥（可选，用于认证）
     */
    private final String apiKey;

    /**
     * 客户端自行创建 HttpClient 时使用的连接超时时间；自定义 HttpClient 由调用方配置。
     */
    @Builder.Default
    private final Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * 读取超时时间
     */
    @Builder.Default
    private final Duration readTimeout = Duration.ofSeconds(30);

    /**
     * WebSocket握手超时时间；设为null时使用HttpClient默认行为
     */
    @Builder.Default
    private final Duration websocketTimeout = Duration.ofMinutes(30);

    /**
     * 自定义HttpClient（可选）
     */
    private final HttpClient httpClient;
}

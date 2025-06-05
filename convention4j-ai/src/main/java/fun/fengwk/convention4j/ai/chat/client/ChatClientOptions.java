package fun.fengwk.convention4j.ai.chat.client;

import fun.fengwk.convention4j.common.http.client.HttpClientFactory;
import lombok.Data;

import java.net.URI;
import java.time.Duration;

/**
 * @author fengwk
 */
@Data
public class ChatClientOptions {

    /**
     * 基础uri
     */
    private URI chatCompletionsUrl;

    /**
     * deekseek token
     */
    private String token;

    /**
     * 每次请求超时
     */
    private Duration perHttpRequestTimeout = HttpClientFactory.getDefaultTimeout();

    /**
     * 一轮对话中最大的 function call 调用次数 (可以避免模型幻觉导致的无限制调用)
     */
    private int maxFunctionCallTimes = 10;

}
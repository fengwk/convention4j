package fun.fengwk.convention4j.common.chat.client;

import fun.fengwk.convention4j.common.chat.request.ChatRequest;
import fun.fengwk.convention4j.common.chat.response.ChatResponse;
import fun.fengwk.convention4j.common.chat.util.ChatUtils;
import fun.fengwk.convention4j.common.http.client.HttpClientFactory;
import fun.fengwk.convention4j.common.http.client.HttpClientUtils;
import fun.fengwk.convention4j.common.http.client.HttpSendResult;
import fun.fengwk.convention4j.common.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Chat GPT 客户端
 *
 * @author fengwk
 */
@Slf4j
public class HttpClientChatClient implements ChatClient {

    private volatile ChatClientOptions clientOptions;
    private final HttpClient httpClient;

    public HttpClientChatClient(ChatClientOptions chatClientOptions) {
        this(chatClientOptions, HttpClientFactory.getDefaultHttpClient());
    }

    public HttpClientChatClient(ChatClientOptions clientOptions, HttpClient httpClient) {
        this.clientOptions = clientOptions;
        this.httpClient = httpClient;
    }

    @Override
    public ChatClientOptions getDefaultClientOptions() {
        return clientOptions;
    }

    @Override
    public void setDefaultClientOptions(ChatClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    @Override
    public ChatCompletionsResponse chatCompletions(ChatRequest chatRequest, ChatClientOptions clientOptions) {
        HttpRequest httpRequest = buildChatCompletionHttpRequest(chatRequest, clientOptions);

        log.debug("chatCompletions request, url: {}, chatRequest: {}",
            clientOptions.getChatCompletionsUrl(), JsonUtils.toJson(chatRequest));

        try (HttpSendResult result = HttpClientUtils.send(httpClient, httpRequest)) {
            if (result.hasError()) {
                Throwable error = result.getError();
                log.error("chatCompletions error", error);
                return new ChatCompletionsResponse(false, null, error.getMessage(), error);
            } else if (result.is2xx()) {
                try {
                    String body = result.parseBodyString();
                    ChatResponse chatResponse = JsonUtils.fromJson(body, ChatResponse.class);
                    return new ChatCompletionsResponse(true, chatResponse, null, null);
                } catch (IOException ex) {
                    log.error("chatCompletions parse body failed", ex);
                    return new ChatCompletionsResponse(false, null, ex.getMessage(), ex);
                }
            } else {
                int statusCode = result.getStatusCode();
                String body = result.tryParseBodyString();
                log.error("chatCompletions failed, status: {}, body: {}", statusCode, body);
                IllegalStateException ex = new IllegalStateException("chatCompletions http status: " + statusCode);
                return new ChatCompletionsResponse(false, null, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public CompletableFuture<ChatCompletionsResponse> streamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener, ChatClientOptions clientOptions) {

        chatRequest.setStream(true);
        ChatUtils.streamIncludeUsage(chatRequest);

        HttpRequest httpRequest = buildChatCompletionHttpRequest(chatRequest, clientOptions);

        log.debug("streamChatCompletions request, url: {}, chatRequest: {}",
            clientOptions.getChatCompletionsUrl(), chatRequest);

        CompleteResponseStreamChatListener completeChatListener = new CompleteResponseStreamChatListener(chatListener);
        return HttpClientUtils.sendAsyncWithSSEListener(httpRequest, completeChatListener)
            .thenApply(resp -> {
                if (resp.hasError()) {
                    log.error("streamChatCompletions request error", resp.getError());
                } else if (!resp.is2xx()) {
                    log.error("streamChatCompletions request failed, body: {}", resp.tryParseBodyString());
                }
                return completeChatListener.toChatCompletionsResponse();
            });
    }

    private HttpRequest buildChatCompletionHttpRequest(ChatRequest chatRequest, ChatClientOptions clientOptions) {
        // body
        String reqBody = JsonUtils.toJson(chatRequest);
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(reqBody, StandardCharsets.UTF_8);
        // request
        return HttpRequest.newBuilder()
            .uri(clientOptions.getChatCompletionsUrl())
            .POST(bodyPublisher)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + clientOptions.getToken())
            .timeout(clientOptions.getPerHttpRequestTimeout())
            .build();
    }

}

package fun.fengwk.convention4j.ai.chat.client;

import fun.fengwk.convention4j.ai.chat.request.ChatRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Chat GPT 客户端
 *
 * @author fengwk
 */
public interface ChatClient {

    /**
     * 获取默认的ClientOptions
     *
     * @return 默认的ClientOptions
     */
    ChatClientOptions getDefaultClientOptions();

    /**
     * 设置默认的ClientOptions
     *
     * @param clientOptions 默认的ClientOptions
     */
    void setDefaultClientOptions(ChatClientOptions clientOptions);

    /**
     * <a href="https://platform.openai.com/docs/api-reference/chat/create">Create chat completion</a>
     *
     * @param chatRequest completions请求
     * @return completions响应，如果请求失败将返回null
     */
    default ChatCompletionsResponse chatCompletions(ChatRequest chatRequest) {
        return chatCompletions(chatRequest, getDefaultClientOptions());
    }

    /**
     * <a href="https://platform.openai.com/docs/api-reference/chat/create">Create chat completion</a>
     *
     * @param chatRequest   completions请求
     * @param clientOptions clientOptions
     * @return completions响应，如果请求失败将返回null
     */
    ChatCompletionsResponse chatCompletions(ChatRequest chatRequest, ChatClientOptions clientOptions);

    /**
     * 流式调用
     *
     * @param chatRequest  completions请求
     * @param chatListener 流式请求监听器
     * @return CompletableFuture
     */
    default CompletableFuture<ChatCompletionsResponse> streamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener) {
        return streamChatCompletions(chatRequest, chatListener, getDefaultClientOptions());
    }

    /**
     * 流式调用
     *
     * @param chatRequest   completions请求
     * @param chatListener  流式请求监听器
     * @param clientOptions clientOptions
     * @return CompletableFuture
     */
    CompletableFuture<ChatCompletionsResponse> streamChatCompletions(
        ChatRequest chatRequest, StreamChatListener chatListener, ChatClientOptions clientOptions);

}

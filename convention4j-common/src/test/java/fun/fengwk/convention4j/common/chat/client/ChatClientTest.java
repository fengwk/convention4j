package fun.fengwk.convention4j.common.chat.client;

import fun.fengwk.convention4j.common.chat.request.ChatMessage;
import fun.fengwk.convention4j.common.chat.request.ChatRequest;
import fun.fengwk.convention4j.common.chat.response.ChatResponse;
import fun.fengwk.convention4j.common.chat.tool.ChatToolHandler;
import fun.fengwk.convention4j.common.chat.tool.ChatToolHandlerRegistry;
import fun.fengwk.convention4j.common.chat.tool.ChatToolHandlerRegistryView;
import fun.fengwk.convention4j.common.chat.tool.DefaultChatToolHandlerRegistry;
import fun.fengwk.convention4j.common.chat.tool.annotation.ChatTool;
import fun.fengwk.convention4j.common.chat.tool.annotation.ChatToolParam;
import fun.fengwk.convention4j.common.chat.util.ChatUtils;
import fun.fengwk.convention4j.common.lang.StringUtils;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fengwk
 */
public class ChatClientTest {

//    @Test
    public void testChatCompletions() {
        ChatClient chatClient = buildHttpChatClient();

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-chat");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("你好")));

        ChatCompletionsResponse chatCompletions = chatClient.chatCompletions(chatRequest);
        Assertions.assertTrue(chatCompletions.isSuccess());
        String content = ChatUtils.getContent(chatCompletions.getChatResponse());
        Assertions.assertTrue(StringUtils.isNotBlank(content));
        System.out.println(content);
    }

//    @Test
    public void testStreamChatCompletions() throws ExecutionException, InterruptedException {
        ChatClient chatClient = buildHttpChatClient();

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-reasoner");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("你好")));

        CompletableFuture<ChatCompletionsResponse> future = chatClient.streamChatCompletions(
            chatRequest, new StreamChatListener() {
            @Override
            public void onReceive(ChatResponse chatResponse) {
                String reasoningContent = ChatUtils.getReasoningContent(chatResponse);
                if (StringUtils.isNotEmpty(reasoningContent)) {
                    System.out.println(reasoningContent);
                } else {
                    String content = ChatUtils.getContent(chatResponse);
                    System.out.println(content);
                }
            }
        });
        ChatCompletionsResponse chatCompletions = future.get();
        Assertions.assertTrue(chatCompletions.isSuccess());
        String content = ChatUtils.getContent(chatCompletions.getChatResponse());
        Assertions.assertTrue(StringUtils.isNotBlank(content));
        System.out.println(content);
    }

//    @Test
    public void testChatCompletionsWithTool() {
        ChatToolHandlerRegistryView registryView = buildToolRegistry();
        ChatClient chatClient = buildHttpChatClient();
        chatClient = new ToolChatClient(chatClient, registryView);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-chat");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("今天天气如何")));

        ChatCompletionsResponse chatCompletions = chatClient.chatCompletions(chatRequest);
        Assertions.assertTrue(chatCompletions.isSuccess());
        String content = ChatUtils.getContent(chatCompletions.getChatResponse());
        Assertions.assertTrue(StringUtils.isNotBlank(content));
        System.out.println(content);
    }

//    @Test
    public void testStreamChatCompletionsWithTool() throws ExecutionException, InterruptedException {
        ChatToolHandlerRegistryView registryView = buildToolRegistry();
        ChatClient chatClient = buildHttpChatClient();
        chatClient = new ToolChatClient(chatClient, registryView);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-chat");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("今天天气如何")));

        CompletableFuture<ChatCompletionsResponse> future = chatClient.streamChatCompletions(
            chatRequest, new FunctionCallStreamChatListener() {

                @Override
                public void onPreFunctionCall(ChatToolHandler handler, String arguments) {
                    System.out.println("执行 function call: " + handler.getName());
                    System.out.println("参数: " + arguments);
                }

                @Override
                public void onPostFunctionCall(ChatToolHandler handler, String arguments, String result) {
                    System.out.println("function call 返回: " + result);
                }

                @Override
                public void onReceive(ChatResponse chatResponse) {
                    String content = ChatUtils.getContent(chatResponse);
                    System.out.println(content);
                }

                @Override
                public void onComplete(ChatCompletionsResponse response) {
                    System.out.println(response);
                }
            });
        ChatCompletionsResponse chatCompletions = future.get();
        Assertions.assertTrue(chatCompletions.isSuccess());
        String content = ChatUtils.getContent(chatCompletions.getChatResponse());
        Assertions.assertTrue(StringUtils.isNotBlank(content));
        System.out.println(content);
    }

    private ChatClient buildHttpChatClient() {
        ChatClientOptions options = new ChatClientOptions();
        options.setChatCompletionsUrl(URI.create("https://api.deepseek.com/chat/completions"));
        options.setToken(System.getenv("OPENAI_API_KEY"));
        return new HttpClientChatClient(options);
    }

    private ChatToolHandlerRegistryView buildToolRegistry() {
        Tools tools = new Tools();

        ChatToolHandlerRegistry registry = new DefaultChatToolHandlerRegistry();
        registry.registerBeanIfNecessary(tools);

        return registry;
    }

    public static class Tools {

        @ChatTool(description = "获取当前日期")
        public String getCurrentDate() {
            System.out.println("获取当前日期: 2025-06-01");
            return "2025-06-01";
        }

        @ChatTool(description = "获取指定日期的天气")
        public String getWeather(
            @ChatToolParam(name = "date", description = "yyyy-MM-dd格式的日期字符串") String date) {
            System.out.println("获取天气: " + date + " 晴");
            return "晴";
        }

    }

}

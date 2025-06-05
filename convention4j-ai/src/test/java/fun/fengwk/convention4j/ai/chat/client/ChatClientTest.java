package fun.fengwk.convention4j.ai.chat.client;

import fun.fengwk.convention4j.ai.chat.request.ChatMessage;
import fun.fengwk.convention4j.ai.chat.request.ChatRequest;
import fun.fengwk.convention4j.ai.chat.response.ChatResponse;
import fun.fengwk.convention4j.ai.tool.*;
import fun.fengwk.convention4j.ai.tool.annotation.ToolFunction;
import fun.fengwk.convention4j.ai.tool.annotation.ToolFunctionParam;
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
        ToolFunctionHandlersView registryView = buildToolRegistry();
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
        ToolFunctionHandlersView registryView = buildToolRegistry();
        ChatClient chatClient = buildHttpChatClient();
        chatClient = new ToolChatClient(chatClient, registryView);

        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("deepseek-chat");
        chatRequest.setMessages(Collections.singletonList(ChatMessage.newUserMessage("今天天气如何")));

        CompletableFuture<ChatCompletionsResponse> future = chatClient.streamChatCompletions(
            chatRequest, new FunctionCallStreamChatListener() {

                @Override
                public void onPreFunctionCall(ToolFunctionHandler handler, String arguments) {
                    System.out.println("执行 function call: " + handler.getName());
                    System.out.println("参数: " + arguments);
                }

                @Override
                public void onPostFunctionCall(ToolFunctionHandler handler, String arguments, String result) {
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

    private ToolFunctionHandlersView buildToolRegistry() {
        Tools tools = new Tools();

        ToolFunctionHandlerRegistry registry = new DefaultChatToolHandlerRegistry();
        ToolFunctionHandlerParser.parseAndRegister(tools, registry);

        return registry;
    }

    public static class Tools {

        @ToolFunction(description = "获取当前日期")
        public String getCurrentDate() {
            System.out.println("获取当前日期: 2025-06-01");
            return "2025-06-01";
        }

        @ToolFunction(description = "获取指定日期的天气")
        public String getWeather(
            @ToolFunctionParam(name = "date", description = "yyyy-MM-dd格式的日期字符串") String date) {
            System.out.println("获取天气: " + date + " 晴");
            return "晴";
        }

    }

}

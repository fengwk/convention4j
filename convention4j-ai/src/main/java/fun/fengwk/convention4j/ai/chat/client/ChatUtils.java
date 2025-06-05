package fun.fengwk.convention4j.ai.chat.client;

import fun.fengwk.convention4j.ai.chat.request.*;
import fun.fengwk.convention4j.ai.chat.response.ChatChoice;
import fun.fengwk.convention4j.ai.chat.response.ChatResponse;
import fun.fengwk.convention4j.ai.chat.response.ChatToolCall;
import fun.fengwk.convention4j.ai.tool.ToolFunctionHandler;
import fun.fengwk.convention4j.ai.tool.ToolFunctionHandlersView;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.util.CollectionUtils;
import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwk
 */
public class ChatUtils {

    private static final String TOOL_CALLS = "tool_calls";
    private static final String FUNCTION = "function";

    private ChatUtils() {}

    public static List<ChatTool> getTools(ToolFunctionHandlersView handlersView) {
        List<ChatTool> chatTools = new ArrayList<>();
        for (String name : handlersView.getAllNames()) {
            ToolFunctionHandler handler = handlersView.getHandler(name);
            if (handler != null) {
                chatTools.add(convert(handler));
            }
        }
        return chatTools;
    }

    public static void streamIncludeUsage(ChatRequest chatRequest) {
        ChatStreamOptions streamOptions = new ChatStreamOptions();
        streamOptions.setInclude_usage(true);
        chatRequest.setStream_options(streamOptions);
    }

    public static ChatMessage getMessage(ChatResponse chatResponse) {
        if (chatResponse == null || CollectionUtils.isEmpty(chatResponse.getChoices())
            || chatResponse.getChoices().get(0) == null) {
            return null;
        }

        ChatChoice chatChoice = chatResponse.getChoices().get(0);
        if (chatChoice.getMessage() != null) {
            return chatChoice.getMessage();
        } else {
            return chatChoice.getDelta();
        }
    }

    public static String getReasoningContent(ChatResponse chatResponse) {
        if (chatResponse == null || CollectionUtils.isEmpty(chatResponse.getChoices())
            || chatResponse.getChoices().get(0) == null) {
            return StringUtils.EMPTY;
        }

        ChatChoice chatChoice = chatResponse.getChoices().get(0);
        if (chatChoice.getMessage() != null) {
            // 普通数据获取的方式
            return NullSafe.of(chatChoice.getMessage().getReasoning_content());
        } else if (chatChoice.getDelta() != null) {
            // 流式数据获取的方式
            return NullSafe.of(chatChoice.getDelta().getReasoning_content());
        }
        return StringUtils.EMPTY;
    }

    public static String getContent(ChatResponse chatResponse) {
        if (chatResponse == null || CollectionUtils.isEmpty(chatResponse.getChoices())
            || chatResponse.getChoices().get(0) == null) {
            return StringUtils.EMPTY;
        }

        ChatChoice chatChoice = chatResponse.getChoices().get(0);
        if (chatChoice.getMessage() != null) {
            // 普通数据获取的方式
            return NullSafe.of(chatChoice.getMessage().getContent());
        } else if (chatChoice.getDelta() != null) {
            // 流式数据获取的方式
            return NullSafe.of(chatChoice.getDelta().getContent());
        }
        return StringUtils.EMPTY;
    }

    public static boolean isFunctionCall(ChatToolCall call) {
        return call != null && FUNCTION.equals(call.getType());
    }

    public static void setFunctionTool(ChatTool tool) {
        if (tool != null) {
            tool.setType(FUNCTION);
        }
    }

    public static boolean isEmptyDelta(ChatResponse chatResponse) {
        if (chatResponse == null) {
            return false;
        }

        List<ChatChoice> choices = chatResponse.getChoices();
        if (CollectionUtils.isEmpty(choices)) {
            return false;
        }

        ChatMessage delta = choices.get(0).getDelta();
        return delta != null && StringUtils.isEmpty(delta.getContent())
            && CollectionUtils.isEmpty(delta.getTool_calls());
    }

    public static boolean isToolCalls(ChatCompletionsResponse response) {
        if (!response.isSuccess()) {
            return false;
        }

        return isToolCalls(response.getChatResponse());
    }

    public static boolean isToolCalls(ChatResponse chatResponse) {
        if (chatResponse == null) {
            return false;
        }

        List<ChatChoice> choices = chatResponse.getChoices();
        if (CollectionUtils.isEmpty(choices)) {
            return false;
        }

        // 对于普通的响应可以通过Finish_reason检查
        if (TOOL_CALLS.equals(choices.get(0).getFinish_reason())) {
            return true;
        }

        // 对于流式响应需要检查是否有tool_calls
        ChatMessage delta = choices.get(0).getDelta();
        return delta != null && CollectionUtils.isNotEmpty(delta.getTool_calls());
    }

    private static ChatTool convert(ToolFunctionHandler handler) {
        ChatTool chatTool = new ChatTool();
        ChatUtils.setFunctionTool(chatTool);

        ChatToolFunction function = new ChatToolFunction();
        function.setDescription(handler.getDescription());
        function.setName(handler.getName());
        function.setParameters(handler.getParameters());
        chatTool.setFunction(function);

        return chatTool;
    }

}

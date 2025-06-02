package fun.fengwk.convention4j.common.chat.client;

import fun.fengwk.convention4j.common.chat.response.ChatResponse;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatCompletionsResponse {

    /**
     * 是否成功
     */
    private final boolean success;

    /**
     * chat response
     */
    private final ChatResponse chatResponse;

    /**
     * 错误信息
     */
    private final String errorMessage;

    /**
     * 错误异常
     */
    private final Throwable error;

    /**
     * 响应是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    public RuntimeException toRuntimeException() {
        if (error instanceof RuntimeException) {
            return (RuntimeException) error;
        } else if (error != null) {
            return new IllegalStateException(formatRuntimeExceptionMessage(), error);
        } else {
            return new IllegalStateException(formatRuntimeExceptionMessage());
        }
    }

    private String formatRuntimeExceptionMessage() {
        String message = "chat completions response error";
        if (errorMessage != null) {
            message = message + ": " + errorMessage;
        }
        return message;
    }

}

package fun.fengwk.convention4j.common.chat.response;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatToolCall {

    /**
     * The ID of the tool call.
     */
    private String id;

    /**
     * The type of the tool. Currently, only function is supported.
     */
    private String type;

    /**
     * The function that the model called.
     */
    private ChatToolCallFunction function;

}

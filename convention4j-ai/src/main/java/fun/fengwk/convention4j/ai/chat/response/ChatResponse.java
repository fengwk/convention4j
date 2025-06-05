package fun.fengwk.convention4j.ai.chat.response;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class ChatResponse {

    /**
     * A unique identifier for the chat completion.
     */
    private String id;

    /**
     * A list of chat completion choices. Can be more than one if n is greater than 1.
     */
    private List<ChatChoice> choices;

    /**
     * The Unix timestamp (in seconds) of when the chat completion was created.
     */
    private Integer created;

    /**
     * The model used for the chat completion.
     */
    private String model;

    /**
     * This fingerprint represents the backend configuration that the model runs with.
     * Can be used in conjunction with the seed request parameter to understand when backend changes have been made that might impact determinism.
     */
    private String system_fingerprint;

    /**
     * The object type, which is always chat.completion.
     */
    private String object;

    /**
     * Usage statistics for the completion request.
     */
    private ChatUsage usage;

}

package fun.fengwk.convention4j.ai.chat.request;

import fun.fengwk.convention4j.ai.tool.JsonSchema;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatToolFunction {

    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    private String description;

    /**
     * Required, The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
     */
    private String name;

    /**
     * The parameters the functions accepts, described as a JSON Schema object. See the guide for examples, and the JSON Schema reference for documentation about the format.
     */
    private JsonSchema parameters;

}

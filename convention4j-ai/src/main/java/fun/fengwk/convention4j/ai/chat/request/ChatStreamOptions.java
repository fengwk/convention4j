package fun.fengwk.convention4j.ai.chat.request;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatStreamOptions {

    /**
     * If set, an additional chunk will be streamed before the data: [DONE] message.
     * The usage field on this chunk shows the token usage statistics for the entire request, and the choices field will always be an empty array.
     * All other chunks will also include a usage field, but with a null value.
     */
    private Boolean include_usage;

}

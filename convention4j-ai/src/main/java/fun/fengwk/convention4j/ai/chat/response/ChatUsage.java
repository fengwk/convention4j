package fun.fengwk.convention4j.ai.chat.response;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ChatUsage {

    /**
     * Number of tokens in the generated completion.
     */
    private Integer completion_tokens;

    /**
     * Number of tokens in the prompt.
     */
    private Integer prompt_tokens;

    /**
     * Total number of tokens used in the request (prompt + completion).
     */
    private Integer total_tokens;

}

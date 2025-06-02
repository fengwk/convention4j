package fun.fengwk.convention4j.common.chat.response;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class ChatLogprobContent extends ChatLogprob {

    /**
     * List of the most likely tokens and their log probability, at this token position. In rare cases, there may be fewer than the number of requested top_logprobs returned.
     */
    private List<ChatLogprob> top_logprobs;

}

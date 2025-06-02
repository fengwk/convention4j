package fun.fengwk.convention4j.common.chat.response;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class ChatLogprobs {

    /**
     * A list of message content tokens with log probability information.
     */
    private List<ChatLogprobContent> content;

}

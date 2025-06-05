package fun.fengwk.convention4j.ai.chat.response;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class ChatLogprob {

    /**
     * The token.
     */
    private String token;

    /**
     * The log probability of this token, if it is within the top 20 most likely tokens. Otherwise, the value -9999.0 is used to signify that the token is very unlikely.
     */
    private Float logprob;

    /**
     * A list of integers representing the UTF-8 bytes representation of the token. Useful in instances where characters are represented by multiple tokens and their byte representations must be combined to generate the correct text representation. Can be null if there is no bytes representation for the token.
     */
    private List<Integer> bytes;

}

package fun.fengwk.convention4j.common.chat.token;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class ChatTokenizerChain implements ChatTokenizer {

    private final List<ChatTokenizer> chain;

    public ChatTokenizerChain(List<ChatTokenizer> chain) {
        this.chain = Objects.requireNonNull(chain);
    }

    @Override
    public boolean support(String modelName) {
        for (ChatTokenizer tokenizer : chain) {
            if (tokenizer.support(modelName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int countTokens(String text, String modelName) {
        for (ChatTokenizer tokenizer : chain) {
            if (tokenizer.support(modelName)) {
                return tokenizer.countTokens(text, modelName);
            }
        }
        throw new IllegalStateException(String.format("current tokenizer [%s] can not support model: %s",
            this, modelName));
    }

}

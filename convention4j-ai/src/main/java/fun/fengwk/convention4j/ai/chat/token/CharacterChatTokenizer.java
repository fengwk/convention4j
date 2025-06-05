package fun.fengwk.convention4j.ai.chat.token;

/**
 * @author fengwk
 */
public class CharacterChatTokenizer implements ChatTokenizer {

    @Override
    public boolean support(String modelName) {
        return true;
    }

    @Override
    public int countTokens(String text, String modelName) {
        return text.length();
    }

}

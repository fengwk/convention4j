package fun.fengwk.convention4j.common.chat.token;

/**
 * 仅实现了估算，<a href="https://api-docs.deepseek.com/zh-cn/quick_start/token_usage">Token 用量计算</a>
 *
 * @author fengwk
 */
public class SimpleDeepSeekChatTokenizer implements ChatTokenizer {

    @Override
    public boolean support(String modelName) {
        return modelName.contains("deepseek");
    }

    @Override
    public int countTokens(String text, String modelName) {
        double cnt = 0f;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c <= 127) {
                cnt += 0.3;
            } else {
                cnt += 0.6;
            }
        }
        return (int) Math.ceil(cnt);
    }

}

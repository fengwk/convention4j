package fun.fengwk.convention4j.ai.chat.token;

/**
 * Token分词器
 *
 * @author fengwk
 */
public interface ChatTokenizer {

    /**
     * 检查是否支持指定模型名称
     *
     * @param modelName 模型名称
     * @return 是否支持
     */
    boolean support(String modelName);

    /**
     * 计算使用指定模型拆分文本的token数量
     *
     * @param text      文本
     * @param modelName 模型名称
     * @return token数量
     * @throws IllegalArgumentException 如果模型名称不支持
     */
    int countTokens(String text, String modelName);

}

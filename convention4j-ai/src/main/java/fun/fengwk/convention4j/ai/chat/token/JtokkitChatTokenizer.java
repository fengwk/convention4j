package fun.fengwk.convention4j.ai.chat.token;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

import java.util.Optional;

/**
 * for ChatGPT
 * see <a href="https://github.com/knuddelsgmbh/jtokkit">jtokkit</a>
 *
 * @author fengwk
 */
public class JtokkitChatTokenizer implements ChatTokenizer {

    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();

    @Override
    public boolean support(String modelName) {
        Optional<ModelType> modelTypeOpt = ModelType.fromName(modelName);
        return modelTypeOpt.isPresent();
    }

    @Override
    public int countTokens(String text, String modelName) {
        Optional<ModelType> modelTypeOpt = ModelType.fromName(modelName);
        if (modelTypeOpt.isEmpty()) {
            throw new IllegalArgumentException("Unsupported model name: " + modelName);
        }
        ModelType modelType = modelTypeOpt.get();
        Encoding encoding = REGISTRY.getEncoding(modelType.getEncodingType());
        return encoding.countTokens(text);
    }

}

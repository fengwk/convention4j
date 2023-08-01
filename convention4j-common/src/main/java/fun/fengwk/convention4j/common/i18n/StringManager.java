package fun.fengwk.convention4j.common.i18n;

import fun.fengwk.convention4j.common.expression.ExpressionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 本地化字符串管理器。
 * 
 * @author fengwk
 */
public class StringManager {

    private final ResourceBundle resourceBundle;
    private final String keyPrefix;

    /**
     *
     * @param resourceBundle not null
     */
    public StringManager(ResourceBundle resourceBundle) {
        this(resourceBundle, null);
    }
    
    /**
     *
     * @param resourceBundle not null
     * @param keyPrefix
     */
    public StringManager(ResourceBundle resourceBundle, String keyPrefix) {
        if (resourceBundle == null) {
            throw new NullPointerException("resourceBundle cannot be null");
        }

        this.resourceBundle = resourceBundle;
        this.keyPrefix = keyPrefix;
    }

    /**
     * 通过key获取字符串。
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, Collections.emptyMap());
    }
    
    /**
     * 通过key获取字符串。
     * 
     * @param key not null
     * @param ctx
     * @return
     */
    public String getString(String key, Map<String, ?> ctx) {
        String str = resourceBundle.getString(realKey(key));
        return ExpressionUtils.format(str, ctx);
    }
    
    private String realKey(String key) {
        return keyPrefix == null ? key : keyPrefix + key;
    }

}

package fun.fengwk.convention4j.common.i18n;

import ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * 本地化字符串管理器。
 * 
 * @author fengwk
 */
public class StringManager {

    private static final Logger log = LoggerFactory.getLogger(StringManager.class);

    private final ResourceBundle resourceBundle;
    private final String keyPrefix;

    /**
     * 构造字符串管理器
     * 
     * @param resourceBundle
     */
    public StringManager(ResourceBundle resourceBundle) {
        this(resourceBundle, null);
    }
    
    /**
     * 构造字符串管理器
     * 
     * @param resourceBundle
     * @param keyPrefix
     */
    public StringManager(ResourceBundle resourceBundle, String keyPrefix) {
        this.resourceBundle = Objects.requireNonNull(resourceBundle);
        this.keyPrefix = keyPrefix;
    }

    /**
     * 通过key获取字符串。
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, null);
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

        try {
            return ExpressionParser.parse(str, ctx == null ? Collections.emptyMap() : ctx);
        } catch (OgnlException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private String realKey(String key) {
        return keyPrefix == null ? key : keyPrefix + key;
    }
    
}

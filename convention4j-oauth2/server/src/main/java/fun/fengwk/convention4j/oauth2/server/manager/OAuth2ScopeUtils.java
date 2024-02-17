package fun.fengwk.convention4j.oauth2.server.manager;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fengwk
 */
public class OAuth2ScopeUtils {

    /**
     * 作用域分隔符
     */
    private static final String SCOPE_SEPARATOR = ",";

    /**
     * 将作用域分割为作用域单元
     *
     * @param scope 完整作用域信息
     * @return 作用域单元列表
     */
    public static Set<String> splitScope(String scope) {
        if (StringUtils.isBlank(scope)) {
            return Collections.emptySet();
        }
        String[] scopeItems = StringUtils.split(scope, SCOPE_SEPARATOR);
        return new HashSet<>(Arrays.asList(scopeItems));
    }

}

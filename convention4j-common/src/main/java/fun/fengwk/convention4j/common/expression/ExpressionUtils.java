package fun.fengwk.convention4j.common.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 表达式工具类。
 *
 * @author fengwk
 */
public class ExpressionUtils {

    private static final String NULL_VALUE = "";

    private ExpressionUtils() {}

    /**
     * 格式化表达式。
     *
     * @param expr 表达式，允许使用嵌套的${}表达式。
     * @param ctx 上下文。
     * @return 返回表达式格式化后的字符串。
     */
    public static String format(String expr, Object ctx) {
        if (expr == null) {
            return null;
        }
        if (ctx == null) {
            ctx = Collections.emptyMap();
        }
        OgnlExpressionParser<Object> parser = new OgnlExpressionParser<>(NULL_VALUE);
        return parser.parse(expr, ctx);
    }

    /**
     * 格式化表达式。
     *
     * @param expr 表达式，允许使用嵌套的${}表达式。
     * @param kvs 键值对，使用(k1, v1), (k2, v2)...形式传递。
     * @return 返回表达式格式化后的字符串。
     */
    public static String formatByKVs(String expr, Object...kvs) {
        if (expr == null) {
            return null;
        }
        Map<Object, Object> ctx = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            ctx.put(kvs[i], i + 1 < kvs.length ? kvs[i + 1] : null);
        }
        return format(expr, ctx);
    }

}

package fun.fengwk.convention4j.common.sql.dynamic;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.sql.dynamic.node.DynamicSqlNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.InterpretContext;
import fun.fengwk.convention4j.common.sql.dynamic.node.InterpretException;
import fun.fengwk.convention4j.common.sql.dynamic.parser.DynamicSqlParser;
import fun.fengwk.convention4j.common.util.NullSafe;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * SqlBuilder允许使用动态sql的方式构建最终的sql语句。
 * 已支持mybatis动态sql中的所有标签：
 * <ul>
 *     <li>if</li>
 *     <li>choose、when、otherwise</li>
 *     <li>trim、where、set</li>
 *     <li>foreach</li>
 * </ul>
 *
 * @see <a href="https://mybatis.net.cn/dynamic-sql.html">动态 SQL</a>
 * @author fengwk
 */
public class DynamicSql {

    private static final Pattern BLANK = Pattern.compile("\\s+");

    private static final ConcurrentMap<String, DynamicSqlNode> CACHE = new ConcurrentHashMap<>();

    private final DynamicSqlNode dynamicSqlNode;
    private Map<String, Object> parameterMap;

    private DynamicSql(DynamicSqlNode dynamicSqlNode) {
        this.dynamicSqlNode = dynamicSqlNode;
    }

    /**
     * 解析动态SQL语句。
     *
     * @param dynamicSql not null
     * @return
     * @throws DynamicSqlException 如果解析的动态SQL语句语法有误，将抛出该异常。
     */
    public static DynamicSql parse(String dynamicSql) {
        Objects.requireNonNull(dynamicSql);

        DynamicSqlNode dynamicSqlNode = CACHE.computeIfAbsent(dynamicSql, s -> {
            try {
                return new DynamicSqlParser().parse(s);
            } catch (SAXException e) {
                throw new DynamicSqlException(e);
            }
        });
        return new DynamicSql(dynamicSqlNode);
    }

    /**
     * 获取当前动态SQL的缓存数。
     *
     * @return
     */
    public static int dynamicSqlCacheSize() {
        return CACHE.size();
    }

    /**
     * 清理所有缓存的动态SQL。
     */
    public static void clearDynamicSqlCache() {
        CACHE.clear();
    }

    /**
     * 添加参数。
     *
     * @param parameterName not empty
     * @param parameter
     * @return
     */
    public DynamicSql addParameter(String parameterName, Object parameter) {
        if (StringUtils.isEmpty(parameterName)) {
            throw new IllegalArgumentException("parameterName cannot be empty");
        }

        if (parameterMap == null) {
            parameterMap = new HashMap<>();
        }
        parameterMap.put(parameterName, parameter);

        return this;
    }

    /**
     * 解析当前入参构造的SQL结果。
     *
     * @return
     */
    public ExecutableSql interpret() {
        InterpretContext ctx = new InterpretContext(parameterMap);
        try {
            dynamicSqlNode.interpret(ctx);
        } catch (InterpretException e) {
            throw new IllegalStateException(e);
        }

        return new ExecutableSql(
                prettySql(ctx.getSql()),
                NullSafe.of(ctx.getParamList()).toArray(new Object[0]));
    }

    private String prettySql(String sql) {
        if (sql == null) {
            return null;
        }

        return BLANK.matcher(sql).replaceAll(" ").trim();
    }

}

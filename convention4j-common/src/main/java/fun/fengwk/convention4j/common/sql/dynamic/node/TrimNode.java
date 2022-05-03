package fun.fengwk.convention4j.common.sql.dynamic.node;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fengwk
 */
public class TrimNode extends AbstractContainerNode {

    /**
     * 表示在trim包裹的SQL前添加指定内容
     */
    private final String prefix;

    /**
     * 表示在trim包裹的SQL末尾添加指定内容
     */
    private final String suffix;

    /**
     * 表示去掉（覆盖）trim包裹的SQL的指定首部内容，多个用|分隔
     */
    private final List<Pattern> prefixOverrides;

    /**
     * 表示去掉（覆盖）trim包裹的SQL的指定尾部内容，多个用|分隔
     */
    private final List<Pattern> suffixOverrides;

    public TrimNode(String prefix, String suffix, String prefixOverrides, String suffixOverrides) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.prefixOverrides = toPrefixOverridePatterns(prefixOverrides);
        this.suffixOverrides = toSuffixOverridePatterns(suffixOverrides);
    }

    private List<Pattern> toPrefixOverridePatterns(String overrides) {
        List<Pattern> overridePatterns = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(overrides, "|");
        while (tokenizer.hasMoreTokens()) {
            String override = tokenizer.nextToken().trim();
            if (!override.isEmpty()) {
                overridePatterns.add(Pattern.compile("^\\s*" + override));
            }
        }
        return overridePatterns;
    }

    private List<Pattern> toSuffixOverridePatterns(String overrides) {
        List<Pattern> overridePatterns = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(overrides, "|");
        while (tokenizer.hasMoreTokens()) {
            String override = tokenizer.nextToken().trim();
            if (!override.isEmpty()) {
                overridePatterns.add(Pattern.compile(override + "\\s*$"));
            }
        }
        return overridePatterns;
    }

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        interpretChildren(ctx);

        String sql = ctx.getSql();

        if (sql != null) {
            for (Pattern p : prefixOverrides) {
                Matcher m = p.matcher(sql);
                if (m.find()) {
                    sql = m.replaceFirst("");
                    break;
                }
            }
        }

        if (sql != null) {
            for (Pattern p : suffixOverrides) {
                Matcher m = p.matcher(sql);
                if (m.find()) {
                    sql = m.replaceFirst("");
                    break;
                }
            }
        }

        if (prefix != null) {
            sql = sql == null ? prefix : prefix + sql;
        }

        if (suffix != null) {
            sql = sql == null ? suffix : sql + suffix;
        }

        ctx.setSql(sql);

        return true;
    }

}

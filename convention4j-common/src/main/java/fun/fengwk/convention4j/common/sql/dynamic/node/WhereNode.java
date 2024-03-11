package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * @author fengwk
 */
public class WhereNode extends AbstractContainerNode {

    private static final Pattern REPLACER = Pattern.compile("^\\s*(and|AND|or|OR)");
    private static final Pattern WHERE = Pattern.compile("^\\s*(where|WHERE)");

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        interpretChildren(ctx);

        String sql = ctx.getSql();
        if (StringUtils.isNotBlank(sql)) {
            sql = REPLACER.matcher(sql).replaceFirst("");
            if (!WHERE.matcher(sql).find()) {
                sql = "where " + sql;
            }
            ctx.setSql(sql);
        }

        return true;
    }

}

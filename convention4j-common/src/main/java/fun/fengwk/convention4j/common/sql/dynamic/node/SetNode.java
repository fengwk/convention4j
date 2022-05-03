package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.StringUtils;

import java.util.regex.Pattern;

/**
 * @author fengwk
 */
public class SetNode extends AbstractContainerNode {

    private static final Pattern REPLACER = Pattern.compile(",\\s*$");
    private static final Pattern SET = Pattern.compile("^\\s*(set|SET)");

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        interpretChildren(ctx);

        String sql = ctx.getSql();
        if (StringUtils.isNotBlank(sql)) {
            sql = REPLACER.matcher(sql).replaceFirst("");
            if (!SET.matcher(sql).find()) {
                sql = "set " + sql;
            }
            ctx.setSql(sql);
        }

        return true;
    }

}

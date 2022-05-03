package fun.fengwk.convention4j.common.sql.dynamic;

import java.util.Arrays;
import java.util.Objects;

/**
 * 可执行的SQL，动态SQL的解释结果。
 *
 * @author fengwk
 */
public class ExecutableSql {

    private final String sql;
    private final Object[] parameters;

    public ExecutableSql(String sql, Object[] parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    public String getSql() {
        return sql;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutableSql result = (ExecutableSql) o;
        return Objects.equals(sql, result.sql) && Arrays.equals(parameters, result.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sql);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "InterpretResult{" +
                "sql='" + sql + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

}

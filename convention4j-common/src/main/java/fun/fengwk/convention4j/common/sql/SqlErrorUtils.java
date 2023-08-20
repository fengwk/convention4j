package fun.fengwk.convention4j.common.sql;

import javax.annotation.Nullable;
import java.sql.SQLException;

/**
 * @author fengwk
 */
public class SqlErrorUtils {

    private SqlErrorUtils() {}

    /**
     * 查找异常及造成原因是否包含{@link SQLException}，如果包含返回该{@link SQLException}，否则返回null。
     *
     * @param t
     * @return
     */
    @Nullable
    public static SQLException findSqlException(Throwable t) {
        while (t != null && !(t instanceof SQLException) && t.getCause() != t) {
            t = t.getCause();
        }

        return (t instanceof SQLException) ? (SQLException) t : null;
    }

}

package fun.fengwk.convention4j.common.sql;

/**
 * @author fengwk
 */
public enum OnDuplicateKeyUpdateResult {

    /**
     * 说明没有冲突数据，正常插入了数据
     */
    INSERT,

    /**
     * 说明存在冲突，但update后的数据与原记录一致，因此0行数据发生改变
     */
    NO_UPDATE,

    /**
     * 说明存在冲突，对冲突数据进行了update操作
     */
    UPDATED;

    /**
     * 从changeRows获取当前枚举
     *
     * @param changedRows 数据库返回的当次操作改变的行数
     * @param opRows 操作的行数
     * @return OnDuplicateKeyUpdateResult
     */
    public static OnDuplicateKeyUpdateResult fromChangedRows(int changedRows, int opRows) {
        if (changedRows == 0) {
            return NO_UPDATE;
        } else if (changedRows == opRows) {
            return INSERT;
        } else {
            return UPDATED;
        }
    }

    /**
     * 从changeRows获取当前枚举，仅适用于单条记录的OnDuplicateKeyUpdate操作
     *
     * @param changedRows 数据库返回的当次操作改变的行数
     * @return OnDuplicateKeyUpdateResult
     */
    public static OnDuplicateKeyUpdateResult fromChangedRows(int changedRows) {
        return fromChangedRows(changedRows, 1);
    }

}

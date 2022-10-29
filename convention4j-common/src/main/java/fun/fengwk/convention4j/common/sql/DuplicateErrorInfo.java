package fun.fengwk.convention4j.common.sql;

/**
 * 索引重复冲突。
 *
 * @author fengwk
 */
public class DuplicateErrorInfo {

    /**
     * 例如"Duplicate entry '1234567890' for key 'uk_mobile'"中解析出"uk_mobile"
     */
    private final String key;

    /**
     * 例如"Duplicate entry '1234567890' for key 'uk_mobile'"中的"1234567890"
     */
    private final String entry;

    DuplicateErrorInfo(String key, String entry) {
        this.key = key;
        this.entry = entry;
    }

    public String getKey() {
        return key;
    }

    public String getEntry() {
        return entry;
    }

}

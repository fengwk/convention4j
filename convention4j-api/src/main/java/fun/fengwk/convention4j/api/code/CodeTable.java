package fun.fengwk.convention4j.api.code;

import java.util.Objects;

/**
 * 错误码码表。
 *
 * @author fengwk
 */
public interface CodeTable {

    /**
     * 获取错误码。
     *
     * @return
     */
    String getCode();

    /**
     * 检查是否与指定编码具有相同的编码值。
     *
     * @param code
     * @return
     */
    default boolean equalsCode(Code code) {
        return Objects.equals(getCode(), code.getCode());
    }

    /**
     * 检查是否与指定编码值相同。
     *
     * @param code
     * @return
     */
    default boolean equalsCode(String code) {
        return Objects.equals(getCode(), code);
    }

}

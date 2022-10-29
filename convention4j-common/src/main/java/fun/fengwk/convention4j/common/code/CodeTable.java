package fun.fengwk.convention4j.common.code;

import java.util.Objects;

/**
 * 状态码码表，用于描述状态码值。实现时可以参考{@link CommonCodeTable}，这是一个典型的状态码码表实现。
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

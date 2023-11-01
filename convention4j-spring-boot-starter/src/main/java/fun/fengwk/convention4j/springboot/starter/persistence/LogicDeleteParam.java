package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.UpdateIncrement;
import fun.fengwk.convention4j.common.IntBool;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class LogicDeleteParam<ID> {

    /**
     * 主键。
     */
    private ID id;

    /**
     * 数据版本号。
     */
    @UpdateIncrement
    private Long version;

    /**
     * 逻辑删除位，0-未删除，1-已删除。
     */
    private final int deleted = IntBool.TRUE;

    /**
     * 构建逻辑删除参数。
     */
    public static <ID> LogicDeleteParam<ID> of(ID id, Long version) {
        LogicDeleteParam<ID> param = new LogicDeleteParam<>();
        param.setId(id);
        param.setVersion(version);
        return param;
    }

}

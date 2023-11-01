package fun.fengwk.convention4j.springboot.starter.persistence;

import lombok.Data;

/**
 * 基础的持久化对象。
 *
 * @author fengwk
 */
@Data
public abstract class BasePO<ID> {

    /**
     * 主键。
     */
    private ID id;

}

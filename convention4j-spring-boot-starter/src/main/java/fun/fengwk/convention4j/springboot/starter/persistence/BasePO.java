package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.Id;
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
    @Id
    private ID id;

}

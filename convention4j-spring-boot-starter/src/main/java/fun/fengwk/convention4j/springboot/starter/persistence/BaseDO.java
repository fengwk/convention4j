package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.Id;
import lombok.Data;

/**
 * 基础的数据层对象。
 *
 * @author fengwk
 */
@Data
public abstract class BaseDO<ID> {

    /**
     * 主键。
     */
    @Id
    private ID id;

}
